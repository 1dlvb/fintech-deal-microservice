package com.fintech.deal;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
@SpringBootApplication
@EnableFeignClients
public class DealApplication {

    public static void main(String[] args) {

        Dotenv dotenv = Dotenv.load();
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
        SpringApplication.run(DealApplication.class, args);

    }

}
