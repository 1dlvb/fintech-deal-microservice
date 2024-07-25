package com.fintech.deal.quartz.job;

import com.fintech.deal.service.ContractorOutboxService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

/**
 * Quartz job for resending failed messages.
 * The job is executed based on the configured cron schedule.
 * @author Matushkin Anton
 */
@RequiredArgsConstructor
public class ResendFailedMessagesJob implements Job {

    @NonNull
    private final ContractorOutboxService contractorOutboxService;

    /**
     * This method is called by the Quartz scheduler to perform the job's task.
     * @param context the job execution context provided by Quartz.
     */
    @Override
    public void execute(JobExecutionContext context) {
        contractorOutboxService.resendFailedMessage();
    }

}
