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

@Configuration
public class QuartzConfig {

    @Value("${deal.resend.failed.message.cron}")
    private String cronExpression;

    @Bean
    public JobDetail jobDetail() {
        return JobBuilder.newJob(ResendFailedMessagesJob.class)
                .withIdentity("resendFailedMessages")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger trigger(JobDetail jobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity("resendFailedMessagesTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .build();
    }

}
