package com.fintech.deal.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fintech.deal.model.DealContractor;
import com.fintech.deal.util.WhenUpdateMainBorrowerInvoked;

/**
 * Service interface for handling operations related to contractor outbox messages.
 * @author Matushkin Anton
 */
public interface ContractorOutboxService {

    /**
     * Updates the status of the main borrower in a Contractor Service based on the given contractor and flag.
     * @param contractor The {@link DealContractor} whose status is to be updated.
     * @param hasMainDeals A boolean flag indicating whether the contractor has main deals.
     * @param whenInvoked An instance of {@link WhenUpdateMainBorrowerInvoked} to be invoked
     * when the update operation is complete.
     */
    void updateMainBorrower(DealContractor contractor, boolean hasMainDeals, WhenUpdateMainBorrowerInvoked whenInvoked) throws JsonProcessingException;

    /**
     * Resends messages that failed to be sent previously.
     */
    void resendFailedMessage();

}
