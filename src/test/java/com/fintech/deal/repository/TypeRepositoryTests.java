package com.fintech.deal.repository;

import com.fintech.deal.config.DealConfig;
import com.fintech.deal.feign.config.FeignConfig;
import com.fintech.deal.model.DealType;
import com.fintech.deal.quartz.config.QuartzConfig;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Import({DealConfig.class, QuartzConfig.class, FeignConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@Transactional
class TypeRepositoryTests {

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
    private TypeRepository typeRepository;

    private DealType savedDealType;

    @BeforeEach
    void setUp() {
        DealType dealType = new DealType("TYPE1", "type1", true);
        savedDealType = typeRepository.save(dealType);
    }

    @Test
    void testGetDealTypeByIdReturnsProperDealType() {
        DealType foundDealType = typeRepository.getDealTypeById(savedDealType.getId());
        assertThat(foundDealType).isNotNull();
        assertThat(foundDealType.getId()).isEqualTo(savedDealType.getId());
        assertThat(foundDealType.getName()).isEqualTo(savedDealType.getName());
        assertThat(foundDealType.isActive()).isEqualTo(savedDealType.isActive());
    }
}
