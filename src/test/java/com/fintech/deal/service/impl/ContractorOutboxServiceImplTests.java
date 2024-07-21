package com.fintech.deal.service.impl;

import com.fintech.deal.dto.MainBorrowerDTO;
import com.fintech.deal.feign.ContractorFeignClient;
import com.fintech.deal.model.ContractorOutboxMessage;
import com.fintech.deal.model.Deal;
import com.fintech.deal.model.DealContractor;
import com.fintech.deal.model.DealStatus;
import com.fintech.deal.model.MessageStatus;
import com.fintech.deal.repository.ContractorOutboxRepository;
import com.fintech.deal.util.WhenUpdateMainBorrowerInvoked;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ContractorOutboxServiceImplTests {

    @Mock
    private ContractorFeignClient feignClient;

    @Mock
    private ContractorOutboxRepository repository;

    @InjectMocks
    private ContractorOutboxServiceImpl contractorOutboxService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpdateMainBorrower_Success() {
        DealContractor contractor = new DealContractor();
        contractor.setContractorId("123");

        ResponseEntity<Void> responseEntity = new ResponseEntity<>(HttpStatus.OK);
        when(feignClient.updateActiveMainBorrower(any(MainBorrowerDTO.class))).thenReturn(responseEntity);

        contractorOutboxService.updateMainBorrower(contractor, true, WhenUpdateMainBorrowerInvoked.ON_UPDATE_STATUS_ACTIVE);

        ArgumentCaptor<ContractorOutboxMessage> captor = ArgumentCaptor.forClass(ContractorOutboxMessage.class);
        verify(repository).save(captor.capture());

        ContractorOutboxMessage savedMessage = captor.getValue();
        assertThat(savedMessage.getContractorId()).isEqualTo("123");
        assertThat(savedMessage.isActiveMainBorrower()).isTrue();
        assertThat(savedMessage.getStatus()).isEqualTo(MessageStatus.SUCCESS);
        assertTrue(savedMessage.isSent());
    }

    @Test
    void testUpdateMainBorrowerFailure() {
        DealContractor contractor = new DealContractor();
        contractor.setContractorId("123");

        ResponseEntity<Void> responseEntity = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        when(feignClient.updateActiveMainBorrower(any(MainBorrowerDTO.class))).thenReturn(responseEntity);
        contractorOutboxService.updateMainBorrower(
                contractor,
                true,
                WhenUpdateMainBorrowerInvoked.ON_UPDATE_STATUS_ACTIVE);
        ArgumentCaptor<ContractorOutboxMessage> captor = ArgumentCaptor.forClass(ContractorOutboxMessage.class);
        verify(repository).save(captor.capture());

        ContractorOutboxMessage savedMessage = captor.getValue();
        assertThat(savedMessage.getContractorId()).isEqualTo("123");
        assertThat(savedMessage.isActiveMainBorrower()).isTrue();
        assertThat(savedMessage.getStatus()).isEqualTo(MessageStatus.FAILED);
        assertFalse(savedMessage.isSent());
    }

    @Test
    void testResendFailedMessageActuallyResends() {
        List<ContractorOutboxMessage> failedMessages = new ArrayList<>();
        ContractorOutboxMessage message = new ContractorOutboxMessage();
        message.setContractorId("123");
        message.setActiveMainBorrower(true);
        message.setContent("Update contractor with ID 123");
        message.setSent(false);
        failedMessages.add(message);

        when(repository.findBySentFalse()).thenReturn(failedMessages);

        DealStatus closedStatus = new DealStatus("CLOSED", "Closed", true);
        Deal deal = new Deal();
        deal.setStatus(closedStatus);

        when(repository.findDealsByContractorId("123")).thenReturn(deal);
        when(feignClient.updateActiveMainBorrower(any(MainBorrowerDTO.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        contractorOutboxService.resendFailedMessage();

        verify(repository).save(message);
        assertTrue(message.isSent());
        assertThat(message.getStatus()).isEqualTo(MessageStatus.SUCCESS);
    }

    @Test
    void testShouldResendWhenFailedOnActiveAndSuccessOnCloseReturnsFalse() {
        String contractorId = "123";
        ContractorOutboxMessage message = new ContractorOutboxMessage();
        message.setContractorId(contractorId);
        message.setContent("Update main borrower: Contractor ID 123 Has main deals true ON_UPDATE_STATUS_ACTIVE Status code 200");
        message.setSent(false);
        message.setStatus(MessageStatus.FAILED);
        // Create a Deal with a CLOSED status
        DealStatus closedStatus = new DealStatus("CLOSED", "Closed", true);
        Deal deal = new Deal();
        deal.setStatus(closedStatus);

        // Mock the repository to return the Deal
        when(repository.findDealsByContractorId(contractorId)).thenReturn(deal);

        // Call the method under test
        boolean result = contractorOutboxService.shouldResend(contractorId, message);

        // Assert results
        assertFalse(result);
        assertTrue(message.isSent());
        assertThat(message.getStatus()).isEqualTo(MessageStatus.SUCCESS);

    }

    @Test
    void testShouldResendWhenFailedOnOtherCases() {
        String contractorId = "123";
        ContractorOutboxMessage message = new ContractorOutboxMessage();
        message.setContractorId(contractorId);
        message.setContent("Update main borrower: Contractor ID 123 Has main deals true ON_UPDATE_STATUS_ACTIVE Status code 200");
        message.setSent(false);
        message.setStatus(MessageStatus.FAILED);
        DealStatus closedStatus = new DealStatus("ACTIVE", "Active", true);
        Deal deal = new Deal();
        deal.setStatus(closedStatus);

        when(repository.findDealsByContractorId(contractorId)).thenReturn(deal);

        boolean result = contractorOutboxService.shouldResend(contractorId, message);

        assertTrue(result);
        assertTrue(!message.isSent());
        assertThat(message.getStatus()).isEqualTo(MessageStatus.FAILED);

    }
}
