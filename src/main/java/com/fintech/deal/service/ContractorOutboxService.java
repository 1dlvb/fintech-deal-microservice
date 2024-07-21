package com.fintech.deal.service;

import com.fintech.deal.model.DealContractor;
import com.fintech.deal.util.WhenUpdateMainBorrowerInvoked;

public interface ContractorOutboxService {

    void updateMainBorrower(DealContractor contractor, boolean hasMainDeals, WhenUpdateMainBorrowerInvoked whenInvoked);

    void resendFailedMessage();

}
