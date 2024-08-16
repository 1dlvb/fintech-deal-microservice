package com.fintech.deal.repository.specification;

import com.fintech.deal.config.DealConfig;
import com.fintech.deal.model.ContractorRole;
import com.fintech.deal.model.Deal;
import com.fintech.deal.model.DealContractor;
import com.fintech.deal.model.DealContractorRole;
import com.fintech.deal.model.DealStatus;
import com.fintech.deal.model.DealType;
import com.fintech.deal.payload.SearchDealPayload;
import com.fintech.deal.quartz.config.QuartzConfig;
import com.fintech.deal.repository.DealRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Import({DealConfig.class, QuartzConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@Transactional
class DealSpecificationTests {

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
    private DealRepository dealRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        DealType dealType = new DealType("CREDIT", "credit", true);
        entityManager.persist(dealType);

        DealStatus dealStatus = new DealStatus("ACTIVE", "active", true);
        entityManager.persist(dealStatus);

        ContractorRole contractorRole = new ContractorRole("BORROWER",
                "Заемщик",
                "BORROWER",
                true);

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

        DealContractor dealContractor = DealContractor.builder()
                .deal(deal)
                .contractorId(UUID.randomUUID().toString())
                .name("Contractor Name")
                .inn("1234567890")
                .main(true)
                .isActive(true)
                .build();
        entityManager.persist(dealContractor);

        DealContractorRole dealContractorRole = new DealContractorRole();
        dealContractorRole.setDealContractor(dealContractor);
        dealContractorRole.setContractorRole(contractorRole);
        dealContractorRole.setActive(true);
        entityManager.persist(dealContractorRole);

        entityManager.flush();
        entityManager.clear();
    }

    private void setupSecurityContext(String... roles) {
        UserDetails user = User.withUsername("testUser")
                .password("password")
                .authorities(Arrays.stream(roles).map(SimpleGrantedAuthority::new).toList())
                .build();
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
        SecurityContextHolder.setContext(context);
    }

    @Test
    void testSearchDealsWithAllFilters() {
        setupSecurityContext("SUPERUSER");
        SearchDealPayload payload = new SearchDealPayload(
                null,
                "Test Deal",
                "Agreement123",
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 12, 31),
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 12, 31),
                Collections.singletonList(new DealType("CREDIT", "credit", true)),
                Collections.singletonList(new DealStatus("ACTIVE", "active", true)),
                LocalDateTime.of(2023, 6, 1, 10, 0),
                LocalDateTime.of(2023, 6, 1, 10, 0),
                "12345"
        );
        Specification<Deal> spec = DealSpecification.searchDeals(payload);
        List<Deal> results = dealRepository.findAll(spec);

        assertFalse(results.isEmpty());
    }

    @Test
    void testSearchDealsWithCreditRoleAndPayloadIsFullReturnsEmptyList() {
        setupSecurityContext("CREDIT_USER");

        SearchDealPayload payload = new SearchDealPayload(
                null,
                "Test Deal",
                "Agreement123",
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 12, 31),
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 12, 31),
                Collections.singletonList(new DealType("CREDIT", "credit", true)),
                Collections.singletonList(new DealStatus("ACTIVE", "active", true)),
                LocalDateTime.of(2023, 6, 1, 10, 0),
                LocalDateTime.of(2023, 6, 1, 10, 0),
                "1234567890"
        );
        Specification<Deal> spec = DealSpecification.searchDeals(payload);
        List<Deal> results = dealRepository.findAll(spec);

        assertTrue(results.isEmpty());
    }
    @Test
    void testSearchDealsWithCreditRoleAndPayloadIsEmpty() {
        setupSecurityContext("CREDIT_USER");

        SearchDealPayload payload = SearchDealPayload.builder().build();
        Specification<Deal> spec = DealSpecification.searchDeals(payload);
        List<Deal> results = dealRepository.findAll(spec);

        assertFalse(results.isEmpty());
    }
    @Test
    void testSearchDealsWithCreditRoleAndPayloadHasTypeCredit() {
        setupSecurityContext("CREDIT_USER");

        SearchDealPayload payload = SearchDealPayload.builder().type(
                Collections.singletonList(DealType.builder().id("CREDIT").build())).build();
        Specification<Deal> spec = DealSpecification.searchDeals(payload);
        List<Deal> results = dealRepository.findAll(spec);

        assertFalse(results.isEmpty());
    }
    @Test
    void testSearchDealsWithCreditRoleAndPayloadHasTypeOverdraft() {
        setupSecurityContext("CREDIT_USER");

        SearchDealPayload payload = SearchDealPayload.builder().type(
                Collections.singletonList(DealType.builder().id("OVERDRAFT").build())).build();
        Specification<Deal> spec = DealSpecification.searchDeals(payload);
        List<Deal> results = dealRepository.findAll(spec);

        assertTrue(results.isEmpty());
    }

    @Test
    void testSearchDealsWithOverdraftRoleAndPayloadIsFullReturnsEmptyList() {
        setupSecurityContext("OVERDRAFT_USER");

        SearchDealPayload payload = new SearchDealPayload(
                null,
                "Test Deal",
                "Agreement123",
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 12, 31),
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 12, 31),
                Collections.singletonList(new DealType("OVERDRAFT", "overdraft", true)),
                Collections.singletonList(new DealStatus("ACTIVE", "active", true)),
                LocalDateTime.of(2023, 6, 1, 10, 0),
                LocalDateTime.of(2023, 6, 1, 10, 0),
                "1234567890"
        );
        Specification<Deal> spec = DealSpecification.searchDeals(payload);
        List<Deal> results = dealRepository.findAll(spec);

        assertTrue(results.isEmpty());
    }

    @Test
    void testSearchDealsWithBothRolesAndPayloadIsFullReturnsEmptyList() {
        setupSecurityContext("CREDIT_USER", "OVERDRAFT_USER");

        SearchDealPayload payload = new SearchDealPayload(
                null,
                "Test Deal",
                "Agreement123",
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 12, 31),
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 12, 31),
                List.of(new DealType("CREDIT", "credit", true),
                        new DealType("OVERDRAFT", "overdraft", true)),
                Collections.singletonList(new DealStatus("ACTIVE", "active", true)),
                LocalDateTime.of(2023, 6, 1, 10, 0),
                LocalDateTime.of(2023, 6, 1, 10, 0),
                "1234567890"
        );
        Specification<Deal> spec = DealSpecification.searchDeals(payload);
        List<Deal> results = dealRepository.findAll(spec);

        assertTrue(results.isEmpty());
    }

}
