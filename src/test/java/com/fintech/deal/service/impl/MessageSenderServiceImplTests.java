package com.fintech.deal.service.impl;

import com.fintech.deal.service.MessageSenderService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Testcontainers
@SpringBootTest
class MessageSenderServiceImplTests {

    @Container
    static final RabbitMQContainer rabbitMQContainer
            = new RabbitMQContainer("rabbitmq:3.13.6-management");

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", postgreSQLContainer::getDriverClassName);
        registry.add("spring.rabbitmq.host", rabbitMQContainer::getHost);
        registry.add("spring.rabbitmq.port", rabbitMQContainer::getAmqpPort);

        System.setProperty("spring.amqp.deserialization.trust.all", "true");
    }

    @Autowired
    private MessageSenderService messageSenderService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Qualifier("activeMainBorrowerQueue")
    @Autowired
    private Queue queue;

    @BeforeAll
    static void setup() {
        rabbitMQContainer.start();
    }

    @Test
    void testSendAndReceiveMessage() {

        messageSenderService.send("test message");

        Message receivedMessage = rabbitTemplate.receive(queue.getName(), 5000);

        assertNotNull(receivedMessage, "Message should not be null");

        String receivedMessageBody = new String(receivedMessage.getBody());
        assertEquals("test message", receivedMessageBody);
    }

}