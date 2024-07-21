package com.fintech.deal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintech.deal.config.DealConfig;
import com.fintech.deal.dto.ContractorDTO;
import com.fintech.deal.exception.NotActiveException;
import com.fintech.deal.feign.config.FeignConfig;
import com.fintech.deal.model.Deal;
import com.fintech.deal.model.DealContractor;
import com.fintech.deal.model.DealStatus;
import com.fintech.deal.quartz.config.QuartzConfig;
import com.fintech.deal.service.ContractorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({DealConfig.class, QuartzConfig.class, FeignConfig.class})
@SpringBootTest
@Testcontainers
public class ContractorControllerTests {
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
    private MockMvc mockMvc;

    @MockBean
    private ContractorService contractorService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testControllerCreatesNewContractor() throws Exception {
        DealContractor sampleContractor = buildSampleContractor();
        ContractorDTO contractorDTO = ContractorDTO.toDTO(sampleContractor);
        when(contractorService.saveContractor(contractorDTO)).thenReturn(contractorDTO);
        mockMvc.perform(put("/deal-contractor/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                ContractorDTO.builder()
                                        .id(sampleContractor.getId())
                                        .dealId(sampleContractor.getDeal().getId())
                                        .contractorId(sampleContractor.getContractorId())
                                        .name(sampleContractor.getName())
                                        .main(sampleContractor.getMain())
                                        .inn(sampleContractor.getInn())
                                        .build()
                        )))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(contractorDTO)));
    }



    @Test
    public void testDeleteContractor() throws Exception, NotActiveException {
        UUID contractorId = UUID.randomUUID();

        doNothing().when(contractorService).deleteContractor(contractorId);

        mockMvc.perform(delete("/deal-contractor/delete/{id}", contractorId))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteContractorNotActive() throws Exception, NotActiveException {
        UUID contractorId = UUID.randomUUID();

        doThrow(new NotActiveException("Contractor is not active")).when(contractorService).deleteContractor(contractorId);

        mockMvc.perform(delete("/deal-contractor/delete/{id}", contractorId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }


    private DealContractor buildSampleContractor() {
        return DealContractor.builder()
                .id(UUID.randomUUID())
                .contractorId("1")
                .deal(buildSampleDeal())
                .name("Contractor 1")
                .inn("123456")
                .main(true)
                .createDate(LocalDateTime.now())
                .modifyDate(LocalDateTime.now())
                .createUserId("system")
                .modifyUserId("system")
                .isActive(true)
                .build();
    }
    private Deal buildSampleDeal() {
        return Deal.builder()
                .id(UUID.randomUUID())
                .description("Sample deal description")
                .agreementNumber("AG-123456")
                .agreementDate(LocalDate.now())
                .agreementStartDt(LocalDateTime.now())
                .availabilityDate(LocalDate.now().plusDays(1))
                .status(DealStatus.builder().id("ACTIVE").name("Active").build())
                .sum(BigDecimal.valueOf(10000.00))
                .isActive(true)
                .build();

    }
}