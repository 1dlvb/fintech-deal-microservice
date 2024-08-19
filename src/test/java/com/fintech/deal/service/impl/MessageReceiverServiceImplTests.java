package com.fintech.deal.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintech.deal.config.DealConfig;
import com.fintech.deal.config.RabbitMqConfig;
import com.fintech.deal.config.RabbitMqReceiverConfig;
import com.fintech.deal.config.SecurityConfig;
import com.fintech.deal.model.DealContractor;
import com.fintech.deal.repository.ContractorRepository;
import com.fintech.deal.repository.DealRepository;
import com.fintech.deal.service.ContractorService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Testcontainers
@SpringBootTest
@Import({DealConfig.class, RabbitMqConfig.class, RabbitMqReceiverConfig.class, SecurityConfig.class})
class MessageReceiverServiceImplTests {

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
    private ContractorService contractorService;

    @Autowired
    private ContractorRepository contractorRepository;

    @Autowired
    private DealRepository dealRepository;

    @Autowired
    private AmqpTemplate amqpTemplate;
    private final static String CONTRACTOR_EXCHANGE = "contractors_contractor_exchange";
    private final static String ROUTING_KEY = "contractor.update";
    @BeforeAll
    static void setup() {
        rabbitMQContainer.start();
        postgreSQLContainer.start();
    }
    @AfterEach
    void tearDown() {
        contractorRepository.deleteAll();
        dealRepository.deleteAll();
    }
    @Test
    @Sql("/sql/contractor-data.sql")
    void testReceiveContractorUpdatesReceivesMessagesAndSavesIt() {
        String messageBody = """
            {
                "id": "156a445e-cb2c-433c-92ab-c2c89e3763c2",
                "name": "test contractor",
                "inn": "1234567890"
            }
        """;

        amqpTemplate.convertAndSend(CONTRACTOR_EXCHANGE, ROUTING_KEY, messageBody, message -> {
            message.getMessageProperties().setHeader("timestamp", System.currentTimeMillis());
            return message;
        });

        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            List<DealContractor> contractor = contractorService.findAllByContractorId("156a445e-cb2c-433c-92ab-c2c89e3763c2");
            assertNotNull(contractor);
            assertEquals("test contractor", contractor.get(0).getName());
            assertEquals("1234567890", contractor.get(0).getInn());
        });
    }

}