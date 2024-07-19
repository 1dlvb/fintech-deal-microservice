package com.fintech.deal.util;

import com.fintech.deal.dto.MainBorrowerDTO;
import com.fintech.deal.feign.ContractorFeignClient;
import com.fintech.deal.model.DealContractor;
import com.fintech.deal.model.MessageStatus;
import com.fintech.deal.model.OutboxMessage;
import com.fintech.deal.repository.OutboxRepository;
import feign.FeignException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

@RequiredArgsConstructor
public final class OutboxMessageManager {


    @NonNull
    private ContractorFeignClient feignClient;

    @NonNull
    private OutboxRepository outboxMessageRepository;

    public void updateMainBorrower(DealContractor contractor,
                                   boolean hasMainDeals) {
        OutboxMessage outboxMessage = new OutboxMessage();
        try {
            ResponseEntity<Void> response =
                    feignClient.updateActiveMainBorrower(new MainBorrowerDTO(contractor.getContractorId(), hasMainDeals));
            if (response.getStatusCode().is2xxSuccessful()) {
                outboxMessage.setStatus(MessageStatus.SUCCESS);
                outboxMessage.setSent(true);
            } else {
                outboxMessage.setStatus(MessageStatus.FAILED);
            }
            outboxMessage.setContent("Update main borrower: Contractor ID " + contractor.getContractorId()
                    + " Has main deals " + hasMainDeals + " Status " + response.getStatusCode());
        } catch (FeignException fe) {
            outboxMessage.setStatus(MessageStatus.FAILED);
            outboxMessage.setContent("Update main borrower: Contractor ID " + contractor.getContractorId()
                    + " Has main deals " + hasMainDeals + " Status " + fe.status());
        }
        outboxMessageRepository.save(outboxMessage);
    }

}
