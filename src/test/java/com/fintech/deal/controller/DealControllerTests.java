package com.fintech.deal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintech.deal.config.DealConfig;
import com.fintech.deal.dto.ChangeStatusOfDealDTO;
import com.fintech.deal.dto.DealWithContractorsDTO;
import com.fintech.deal.dto.ResponseDealDTO;
import com.fintech.deal.dto.SaveOrUpdateDealDTO;
import com.fintech.deal.quartz.config.QuartzConfig;
import com.fintech.deal.service.DealService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({DealConfig.class, QuartzConfig.class})
@SpringBootTest
@Testcontainers
class DealControllerTests {
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
    private DealService dealService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setupSecurityContext() {
        UserDetails user = User.withUsername("testUser")
                .password("password")
                .authorities(new SimpleGrantedAuthority("SUPERUSER"))
                .build();
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
        SecurityContextHolder.setContext(context);
    }

    @Test
    void testSaveDeal() throws Exception {
        SaveOrUpdateDealDTO saveOrUpdateDealDTO = new SaveOrUpdateDealDTO();
        saveOrUpdateDealDTO.setDescription("description");
        saveOrUpdateDealDTO.setAgreementNumber("123456");

        ResponseDealDTO responseDealDTO = new ResponseDealDTO();
        responseDealDTO.setDescription("description");
        responseDealDTO.setAgreementNumber("123456");

        when(dealService.saveDeal(saveOrUpdateDealDTO)).thenReturn(responseDealDTO);

        mockMvc.perform(put("/deal/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saveOrUpdateDealDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDealDTO)));
    }

    @Test
    void testChangeStatus() throws Exception {
        ChangeStatusOfDealDTO changeStatusOfDealDTO = new ChangeStatusOfDealDTO();
        ResponseDealDTO responseDealDTO = new ResponseDealDTO();

        when(dealService.changeStatus(changeStatusOfDealDTO)).thenReturn(responseDealDTO);

        mockMvc.perform(patch("/deal/change/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changeStatusOfDealDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDealDTO)));
    }

    @Test
    void testGetDealById() throws Exception {
        UUID dealId = UUID.randomUUID();
        DealWithContractorsDTO dealWithContractorsDTO = new DealWithContractorsDTO();

        when(dealService.getDealWithContractorsById(dealId)).thenReturn(dealWithContractorsDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/deal/{id}", dealId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dealWithContractorsDTO)));
    }

}