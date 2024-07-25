package com.fintech.deal.controller;

import com.fintech.deal.config.DealConfig;
import com.fintech.deal.dto.ContractorDTO;
import com.fintech.deal.dto.RoleDTO;
import com.fintech.deal.exception.NotActiveException;
import com.fintech.deal.feign.config.FeignConfig;
import com.fintech.deal.model.ContractorRole;
import com.fintech.deal.model.Deal;
import com.fintech.deal.model.DealContractor;
import com.fintech.deal.model.DealStatus;
import com.fintech.deal.quartz.config.QuartzConfig;
import com.fintech.deal.service.ContractorService;
import jakarta.persistence.EntityNotFoundException;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({DealConfig.class, QuartzConfig.class, FeignConfig.class})
@SpringBootTest
@Testcontainers
class RoleControllerTests {
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


    @Test
    void testAddRole() throws Exception, NotActiveException {
        DealContractor contractor = buildSampleContractor();
        ContractorRole role = new ContractorRole(
                "ROLE",
                "role",
                "ROLE_CATEGORY",
                true);
        RoleDTO roleDTO = RoleDTO.toDTO(role);
        ContractorDTO contractorDTO = ContractorDTO.builder()
                .id(contractor.getId())
                .contractorId(contractor.getContractorId())
                .dealId(contractor.getDeal().getId())
                .inn(contractor.getInn())
                .main(contractor.getMain())
                .roles(Collections.singletonList(role))
                .build();
        when(contractorService.addRole(UUID.fromString(contractor.getContractorId()), roleDTO))
                .thenReturn(contractorDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/contractor-to-role/add/{id}", contractor.getContractorId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""" 
                                {
                                "id": "ROLE"
                            }
                            """))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.contractor_id").value(contractor.getContractorId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.roles[0].id").value("ROLE"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.roles[0].name").value("role"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.roles[0].category").value("ROLE_CATEGORY"));
    }

    @Test
    void testAddRoleRoleNotFound() throws Exception, NotActiveException {
        RoleDTO roleDTO = RoleDTO.toDTO(new ContractorRole(
                "ROLE",
                "role",
                "ROLE_CATEGORY",
                true));

        when(contractorService.addRole(any(UUID.class), eq(roleDTO)))
                .thenThrow(new NotActiveException("Contractor not found"));

        mockMvc.perform(MockMvcRequestBuilders.post("/contractor-to-role/add/{id}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""" 
                                {
                                "id": "ROLE"
                            }
                            """))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andDo(print());
    }

    @Test
    void testDeleteRoleSuccess() throws Exception {
        UUID roleId = UUID.randomUUID();
        doNothing().when(contractorService).deleteRoleByDealContractorRoleId(roleId);
        mockMvc.perform(MockMvcRequestBuilders.delete("/contractor-to-role/delete/{id}", roleId))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andDo(print());
    }

    @Test
    void testDeleteRoleNotFound() throws Exception {
        UUID roleId = UUID.randomUUID();
        doThrow(new EntityNotFoundException("Role not found")).when(contractorService).deleteRoleByDealContractorRoleId(roleId);
        mockMvc.perform(MockMvcRequestBuilders.delete("/contractor-to-role/delete/{id}", roleId))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andDo(print());
    }


    private DealContractor buildSampleContractor() {
        return DealContractor.builder()
                .id(UUID.randomUUID())
                .contractorId(UUID.randomUUID().toString())
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