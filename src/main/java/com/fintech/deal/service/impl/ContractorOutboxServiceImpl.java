package com.fintech.deal.service.impl;

import com.fintech.deal.dto.MainBorrowerDTO;
import com.fintech.deal.feign.ContractorFeignClient;
import com.fintech.deal.model.Deal;
import com.fintech.deal.model.DealContractor;
import com.fintech.deal.model.MessageStatus;
import com.fintech.deal.model.ContractorOutboxMessage;
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
    private ContractorOutboxRepository repository;
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
        } catch (FeignException fe) {
            contractorOutboxMessage.setStatus(MessageStatus.FAILED);
            contractorOutboxMessage.setContent(
                    String.format("Update main borrower: Contractor ID %s Has main deals %s %s Status code %s",
                    contractor.getContractorId(), hasMainDeals, whenInvoked, fe.status()));
        }
        repository.save(contractorOutboxMessage);
    }

    @AuditLog(logLevel = LogLevel.INFO)
    public void resendFailedMessage() {
        List<ContractorOutboxMessage> failedMessages = repository.findBySentFalse();
        Collections.reverse(failedMessages);
        for (ContractorOutboxMessage message : failedMessages) {
            if (shouldResend(message.getContractorId(), message)) {
                try {
                    feignClient.updateActiveMainBorrower(new MainBorrowerDTO(message.getContractorId(), message.isActiveMainBorrower()));
                    message.setStatus(MessageStatus.SUCCESS);
                    message.setSent(true);
                    repository.save(message);
                } catch (Exception ignored) {
                }
            }
        }
    }

    @AuditLog(logLevel = LogLevel.INFO)
    public Boolean shouldResend(String contractorId, ContractorOutboxMessage message) {
        Deal deal = repository.findDealsByContractorId(contractorId);
        if (deal.getStatus().getId().equals("CLOSED")
                && message.getContent().contains(WhenUpdateMainBorrowerInvoked.ON_UPDATE_STATUS_ACTIVE.name())) {
            message.setSent(true);
            message.setStatus(MessageStatus.SUCCESS);
            repository.save(message);
            return false;
        } else {
            return true;
        }
    }

}
