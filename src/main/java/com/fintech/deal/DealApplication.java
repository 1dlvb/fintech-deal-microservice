package com.fintech.deal;

import jakarta.annotation.PostConstruct;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
@EnableScheduling
@SpringBootApplication
public class DealApplication {

    private final RabbitAdmin rabbitAdmin;
    private final Queue queue;

    public DealApplication(RabbitAdmin rabbitAdmin, Queue queue) {
        this.rabbitAdmin = rabbitAdmin;
        this.queue = queue;
    }

    @PostConstruct
    public void declareQueue() {
        rabbitAdmin.declareQueue(queue);
    }

    public static void main(String[] args) {

        SpringApplication.run(DealApplication.class, args);

    }

}

