package com.fintech.deal.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fintech.deal.config.DealConfig;
import com.fintech.deal.dto.ChangeStatusOfDealDTO;
import com.fintech.deal.dto.DealWithContractorsDTO;
import com.fintech.deal.dto.ResponseDealDTO;
import com.fintech.deal.dto.SaveOrUpdateDealDTO;
import com.fintech.deal.model.Deal;
import com.fintech.deal.model.DealContractor;
import com.fintech.deal.model.DealStatus;
import com.fintech.deal.quartz.config.QuartzConfig;
import com.fintech.deal.repository.ContractorRepository;
import com.fintech.deal.repository.DealContractorRoleRepository;
import com.fintech.deal.repository.DealRepository;
import com.fintech.deal.service.ContractorOutboxService;
import com.fintech.deal.service.StatusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@Import({DealConfig.class, QuartzConfig.class})
class DealServiceImplTests {

    @Mock
    private DealRepository dealRepository;

    @Mock
    private ContractorRepository contractorRepository;

    @Mock
    private StatusService statusService;

    @Mock
    private DealContractorRoleRepository dealContractorRoleRepository;

    @Mock
    private ContractorOutboxService outboxService;

    @InjectMocks
    private DealServiceImpl dealService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveDealReturnsNewDeal() {
        SaveOrUpdateDealDTO saveOrUpdateDealDTO = new SaveOrUpdateDealDTO();
        saveOrUpdateDealDTO.setId(null);
        saveOrUpdateDealDTO.setDescription("New Deal");

        Deal deal = new Deal();
        deal.setId(UUID.randomUUID());
        deal.setDescription("New Deal");

        DealStatus draftStatus = new DealStatus();
        draftStatus.setId("DRAFT");

        when(statusService.getStatusById("DRAFT")).thenReturn(draftStatus);
        when(dealRepository.save(any(Deal.class))).thenReturn(deal);

        ResponseDealDTO response = dealService.saveDeal(saveOrUpdateDealDTO);

        assertNotNull(response);
        assertEquals("New Deal", response.getDescription());
        verify(dealRepository, times(1)).save(any(Deal.class));
    }

    @Test
    void testGetDealByIdFound() {
        UUID dealId = UUID.randomUUID();
        Deal deal = new Deal();
        deal.setId(dealId);
        when(dealRepository.findById(dealId)).thenReturn(Optional.of(deal));
        Deal result = dealService.getDealById(dealId);
        assertNotNull(result);
        assertEquals(dealId, result.getId());
    }

    @Test
    void testGetDealByIdNotFound() {
        UUID dealId = UUID.randomUUID();
        when(dealRepository.findById(dealId)).thenReturn(Optional.empty());
        Deal result = dealService.getDealById(dealId);
        assertNull(result);
    }

    @Test
    void testGetDealWithContractorsByIdReturnsDeals() {
        UUID dealId = UUID.randomUUID();
        Deal deal = new Deal();
        deal.setId(dealId);
        DealContractor contractor = new DealContractor();
        contractor.setId(UUID.randomUUID());
        contractor.setDeal(deal);

        when(dealRepository.findById(dealId)).thenReturn(Optional.of(deal));
        when(contractorRepository.findAllByDealId(dealId)).thenReturn(Collections.singletonList(contractor));

        DealWithContractorsDTO result = dealService.getDealWithContractorsById(dealId);

        assertNotNull(result);
        assertEquals(dealId, result.getId());
        assertEquals(1, result.getContractorDTOS().size());
    }

    @Test
    void testGetListOfContractorsByDealIdReturnsListOfContractors() {
        UUID dealId = UUID.randomUUID();
        Deal deal = new Deal();
        deal.setId(dealId);

        DealContractor contractor = new DealContractor();
        contractor.setId(UUID.randomUUID());
        contractor.setDeal(deal);

        when(dealRepository.findById(dealId)).thenReturn(Optional.of(deal));
        when(contractorRepository.findAllByDealId(dealId)).thenReturn(Collections.singletonList(contractor));

        List<DealContractor> result = dealService.getListOfContractorsByDealId(dealId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(dealId, result.get(0).getDeal().getId());
    }


    @Test
    void testChangeStatusSuccess() throws JsonProcessingException {
        UUID dealId = UUID.randomUUID();
        ChangeStatusOfDealDTO changeStatusOfDealDTO = new ChangeStatusOfDealDTO();
        changeStatusOfDealDTO.setId(dealId);
        changeStatusOfDealDTO.setStatus(new DealStatus("ACTIVE", "active", true));

        Deal deal = new Deal();
        deal.setId(dealId);
        deal.setStatus(new DealStatus("DRAFT", "draft", true));

        DealStatus activeStatus = new DealStatus();
        activeStatus.setId("ACTIVE");

        when(dealRepository.findById(dealId)).thenReturn(Optional.of(deal));
        when(statusService.getStatusById("ACTIVE")).thenReturn(activeStatus);
        when(dealRepository.save(any(Deal.class))).thenReturn(deal);

        ResponseDealDTO response = dealService.changeStatus(changeStatusOfDealDTO);

        assertNotNull(response);
        assertEquals("ACTIVE", response.getStatus().getId());
        verify(dealRepository, times(1)).save(deal);
    }



}