package com.fintech.deal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
@SpringBootApplication
@EnableFeignClients
@EnableScheduling
public class DealApplication {

    public static void main(String[] args) {

        SpringApplication.run(DealApplication.class, args);

    }

}
