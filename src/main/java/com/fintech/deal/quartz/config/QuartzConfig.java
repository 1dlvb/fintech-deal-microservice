package com.fintech.deal.quartz.config;

import com.fintech.deal.quartz.job.ResendFailedMessagesJob;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Quartz scheduler.
 * This class configures the Quartz job and trigger for resending failed messages.
 * @author Matushkin Anton
 */
@Configuration
public class QuartzConfig {

    /**
     * Cron expression used to schedule the job.
     */
    @Value("${deal.resend.failed.message.cron}")
    private String cronExpression;

    /**
     * Defines the job detail for the {@link ResendFailedMessagesJob}.
     * @return a {@link JobDetail} instance for the {@link ResendFailedMessagesJob}
     */
    @Bean
    public JobDetail jobDetail() {
        return JobBuilder.newJob(ResendFailedMessagesJob.class)
                .withIdentity("resendFailedMessages")
                .storeDurably()
                .build();
    }

    /**
     * Defines the trigger for the job.
     * @param jobDetail the {@link JobDetail} instance to be associated with the trigger
     * @return a {@link Trigger} instance configured with the cron schedule
     */
    @Bean
    public Trigger trigger(JobDetail jobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity("resendFailedMessagesTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .build();
    }

}
