package com.fintech.deal.service.impl;

import com.fintech.deal.dto.MainBorrowerDTO;
import com.fintech.deal.feign.ContractorFeignClient;
import com.fintech.deal.model.Deal;
import com.fintech.deal.model.DealContractor;
import com.fintech.deal.model.MessageStatus;
import com.fintech.deal.model.ContractorOutboxMessage;
import com.fintech.deal.model.StatusEnum;
import com.fintech.deal.repository.ContractorOutboxRepository;
import com.fintech.deal.service.ContractorOutboxService;
import com.fintech.deal.util.WhenUpdateMainBorrowerInvoked;
import com.onedlvb.advice.LogLevel;
import com.onedlvb.advice.annotation.AuditLog;
import feign.FeignException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * An implementation of {@link ContractorOutboxService}
 * @author Matushkin Anton
 */
@Service
@RequiredArgsConstructor
public class ContractorOutboxServiceImpl implements ContractorOutboxService {


    @NonNull
    private ContractorFeignClient feignClient;

    @NonNull
    private ContractorOutboxRepository contractorOutboxRepository;

    /**
     * Updates the main borrower status for the given contractor and logs the outcome.
     * <p>
     * This method sends a request to the contractor service to update the main borrower status. It records
     * the outcome of the request and saves the message to the outbox repository.
     * @see ContractorFeignClient
     * @param contractor The contractor for whom the main borrower status is being updated.
     * @param hasMainDeals Indicates if the contractor has main deals.
     * @param whenInvoked Specifies when the update was invoked.
     */
    @AuditLog(logLevel = LogLevel.INFO)
    public void updateMainBorrower(DealContractor contractor, boolean hasMainDeals,
                                   WhenUpdateMainBorrowerInvoked whenInvoked) {
        ContractorOutboxMessage contractorOutboxMessage = new ContractorOutboxMessage();
        contractorOutboxMessage.setContractorId(contractor.getContractorId());
        contractorOutboxMessage.setActiveMainBorrower(hasMainDeals);
        try {
            ResponseEntity<Void> response =
                    feignClient.updateActiveMainBorrower(new MainBorrowerDTO(contractor.getContractorId(), hasMainDeals));
            if (response.getStatusCode().is2xxSuccessful()) {
                contractorOutboxMessage.setStatus(MessageStatus.SUCCESS);
                contractorOutboxMessage.setSent(true);
            } else {
                contractorOutboxMessage.setStatus(MessageStatus.FAILED);
            }
            contractorOutboxMessage.setContent(
                    String.format("Update main borrower: Contractor ID %s Has main deals %s %s Status code %s",
                    contractor.getContractorId(), hasMainDeals, whenInvoked, response.getStatusCode()));
            contractorOutboxMessage.setException(null);

        } catch (FeignException fe) {
            contractorOutboxMessage.setStatus(MessageStatus.FAILED);
            contractorOutboxMessage.setContent(
                    String.format("Update main borrower: Contractor ID %s Has main deals %s %s Status code %s",
                    contractor.getContractorId(), hasMainDeals, whenInvoked, fe.status()));
            contractorOutboxMessage.setException(fe.getMessage());
        }
        contractorOutboxRepository.save(contractorOutboxMessage);
    }

    /**
     * Resends messages from the outbox that failed to be sent previously.
     * <p>
     * This method retrieves all failed messages, attempts to resend them, and updates their status
     * based on the outcome of the resend attempt.
     */
    @AuditLog(logLevel = LogLevel.INFO)
    public void resendFailedMessage() {
        List<ContractorOutboxMessage> failedMessages = contractorOutboxRepository.findBySentFalse();
        Collections.reverse(failedMessages);
        for (ContractorOutboxMessage message : failedMessages) {
            if (shouldResend(message.getContractorId(), message)) {
                try {
                    feignClient.updateActiveMainBorrower(new MainBorrowerDTO(message.getContractorId(), message.isActiveMainBorrower()));
                    message.setStatus(MessageStatus.SUCCESS);
                    message.setSent(true);
                    message.setException(null);
                    contractorOutboxRepository.save(message);
                } catch (Exception e) {
                    message.setSent(false);
                    message.setStatus(MessageStatus.FAILED);
                    message.setException(e.getMessage());
                    contractorOutboxRepository.save(message);
                }
            }
        }
    }

    /**
     * Determines whether a message should be resent based on the status of the associated deal.
     * <p>
     * This method checks the status of the deal associated with the contractor ID in the message. If the deal
     * is closed and the message was invoked to update the status to active, the message is marked as sent
     * and its status is updated to success.
     * @param contractorId The ID of the contractor associated with the message.
     * @param message The message to check for resend.
     * @return {@code true} if the message should be resent, {@code false} otherwise.
     */
    @AuditLog(logLevel = LogLevel.INFO)
    public Boolean shouldResend(String contractorId, ContractorOutboxMessage message) {
        Deal deal = contractorOutboxRepository.findDealsByContractorId(contractorId);
        if (deal.getStatus().getId().equals(StatusEnum.CLOSED.name())
                && message.getContent().contains(WhenUpdateMainBorrowerInvoked.ON_UPDATE_STATUS_ACTIVE.name())) {
            message.setSent(true);
            message.setStatus(MessageStatus.SUCCESS);
            message.setException(null);
            contractorOutboxRepository.save(message);
            return false;
        } else {
            return true;
        }
    }

}
