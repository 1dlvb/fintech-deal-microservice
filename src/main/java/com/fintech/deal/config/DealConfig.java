package com.fintech.deal.config;

import com.fintech.deal.auditor.AuditorAwareImpl;
import com.fintech.deal.feign.ContractorFeignClient;
import com.fintech.deal.repository.OutboxRepository;
import com.fintech.deal.util.OutboxMessageManager;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class DealConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return new AuditorAwareImpl();
    }

    @Bean
    public Dotenv dotenv() {
        return Dotenv.configure().load();
    }

    @Bean
    public OutboxMessageManager outboxMessageManager(ContractorFeignClient feignClient, OutboxRepository outboxRepository) {
        return new OutboxMessageManager(feignClient, outboxRepository);
    }

}
