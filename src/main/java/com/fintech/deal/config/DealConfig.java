package com.fintech.deal.config;

import com.fintech.deal.auditor.AuditorAware;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class DealConfig {

    @Bean
    public org.springframework.data.domain.AuditorAware<String> auditorProvider() {
        return new AuditorAware();
    }

    @Bean
    public Dotenv dotenv() {
        return Dotenv.configure().load();
    }

}
