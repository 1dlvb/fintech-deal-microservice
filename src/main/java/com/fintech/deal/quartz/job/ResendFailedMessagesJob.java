package com.fintech.deal.quartz.job;

import com.fintech.deal.service.ContractorOutboxService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

@RequiredArgsConstructor
public class ResendFailedMessagesJob implements Job {

    @NonNull
    private ContractorOutboxService contractorOutboxService;

    @Override
    public void execute(JobExecutionContext context) {
        contractorOutboxService.resendFailedMessage();
    }

}
