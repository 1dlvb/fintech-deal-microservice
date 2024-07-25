package com.fintech.deal.repository;

import com.fintech.deal.config.DealConfig;
import com.fintech.deal.feign.config.FeignConfig;
import com.fintech.deal.model.ContractorOutboxMessage;
import com.fintech.deal.model.ContractorRole;
import com.fintech.deal.model.Deal;
import com.fintech.deal.model.DealContractor;
import com.fintech.deal.model.DealContractorRole;
import com.fintech.deal.model.DealStatus;
import com.fintech.deal.model.DealType;
import com.fintech.deal.model.MessageStatus;
import com.fintech.deal.quartz.config.QuartzConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringJUnitConfig
@ActiveProfiles("test")
@Import({DealConfig.class, QuartzConfig.class, FeignConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
@Testcontainers
class ContractorOutboxRepositoryTests {
    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    public static void setTestProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Autowired
    private ContractorOutboxRepository contractorOutboxRepository;

    @Autowired
    private ContractorRepository contractorRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        DealType dealType = new DealType("TYPE", "type", true);
        entityManager.persist(dealType);

        DealStatus dealStatus = new DealStatus("ACTIVE", "active", true);
        entityManager.persist(dealStatus);

        ContractorRole contractorRole = new ContractorRole("BORROWER", "Заемщик", "BORROWER", true);
        entityManager.persist(contractorRole);

        Deal deal = Deal.builder()
                .description("Test Deal")
                .agreementNumber("Agreement123")
                .agreementDate(LocalDate.of(2023, 1, 1))
                .availabilityDate(LocalDate.of(2023, 12, 31))
                .type(dealType)
                .status(dealStatus)
                .closeDt(LocalDateTime.of(2023, 6, 1, 10, 0))
                .isActive(true)
                .build();
        entityManager.persist(deal);

        ContractorOutboxMessage message = new ContractorOutboxMessage();
        message.setContent("Test message content");
        message.setContractorId("123");
        message.setActiveMainBorrower(true);
        message.setStatus(MessageStatus.FAILED);
        message.setSent(false);
        contractorOutboxRepository.save(message);

        DealContractor dealContractor = DealContractor.builder()
                .deal(deal)
                .contractorId("123")
                .name("Contractor Name")
                .inn("1234567890")
                .main(true)
                .isActive(true)
                .build();
        contractorRepository.save(dealContractor);

        DealContractorRole dealContractorRole = new DealContractorRole();
        dealContractorRole.setDealContractor(dealContractor);
        dealContractorRole.setContractorRole(contractorRole);
        dealContractorRole.setIsActive(true);
        entityManager.persist(dealContractorRole);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void testFindBySentFalseReturnsMessageWithSentEqualsFalse() {
        List<ContractorOutboxMessage> result = contractorOutboxRepository.findBySentFalse();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getContractorId()).isEqualTo("123");
        assertThat(result.get(0).isSent()).isFalse();
    }

    @Test
    void testFindDealsByContractorIdReturnsProperDeal() {
        Deal result = contractorOutboxRepository.findDealsByContractorId("123");

        assertThat(result).isNotNull();
        assertThat(result.getDescription()).isEqualTo("Test Deal");
        assertThat(result.getDealContractors()).isNotNull();
        assertThat(result.getDealContractors()).hasSize(1);
        assertThat(result.getDealContractors().get(0).getContractorId()).isEqualTo("123");
    }

}