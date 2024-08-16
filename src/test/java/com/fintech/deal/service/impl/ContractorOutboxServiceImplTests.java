package com.fintech.deal.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintech.deal.config.DealConfig;
import com.fintech.deal.dto.MainBorrowerDTO;
import com.fintech.deal.model.ContractorOutboxMessage;
import com.fintech.deal.model.Deal;
import com.fintech.deal.model.DealContractor;
import com.fintech.deal.model.DealStatus;
import com.fintech.deal.model.MessageStatus;
import com.fintech.deal.quartz.config.QuartzConfig;
import com.fintech.deal.repository.ContractorOutboxRepository;
import com.fintech.deal.service.MessageSenderService;
import com.fintech.deal.util.WhenUpdateMainBorrowerInvoked;
import lombok.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
@ActiveProfiles("test")
@Import({DealConfig.class, QuartzConfig.class})
class ContractorOutboxServiceImplTests {

    @Mock
    private ContractorOutboxRepository contractorOutboxRepository;
    @Mock
    private MessageSenderService messageSenderService;
    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ContractorOutboxServiceImpl contractorOutboxService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpdateMainBorrowerSuccess() {
        DealContractor contractor = new DealContractor();
        contractor.setContractorId("123");

        contractorOutboxService.updateMainBorrower(contractor, true, WhenUpdateMainBorrowerInvoked.ON_UPDATE_STATUS_ACTIVE);

        ArgumentCaptor<ContractorOutboxMessage> captor = ArgumentCaptor.forClass(ContractorOutboxMessage.class);
        verify(contractorOutboxRepository).save(captor.capture());

        ContractorOutboxMessage savedMessage = captor.getValue();
        assertThat(savedMessage.getContractorId()).isEqualTo("123");
        assertThat(savedMessage.isActiveMainBorrower()).isTrue();
        assertThat(savedMessage.getStatus()).isEqualTo(MessageStatus.SUCCESS);
        assertTrue(savedMessage.isSent());
    }

    @Test
    void testUpdateMainBorrowerFailure() throws JsonProcessingException {
        DealContractor contractor = new DealContractor();
        contractor.setId(UUID.randomUUID());
        contractor.setMain(true);

        doThrow(new RuntimeException("Sending failed")).when(messageSenderService).send(anyString());

        when(objectMapper.writeValueAsString(any(MainBorrowerDTO.class)))
                .thenReturn("{\"contractorId\":\"12345\",\"hasMainDeals\":true}");

        contractorOutboxService.updateMainBorrower(contractor, contractor.isMain(),
                WhenUpdateMainBorrowerInvoked.ON_UPDATE_STATUS_ACTIVE);

        ArgumentCaptor<ContractorOutboxMessage> messageCaptor = ArgumentCaptor.forClass(ContractorOutboxMessage.class);
        verify(contractorOutboxRepository).save(messageCaptor.capture());

        ContractorOutboxMessage capturedMessage = messageCaptor.getValue();

        assertEquals(contractor.getContractorId(), capturedMessage.getContractorId());
        assertFalse(capturedMessage.isSent());
        assertEquals(MessageStatus.FAILED, capturedMessage.getStatus());
        assertNotNull(capturedMessage.getException());
        assertTrue(capturedMessage.getException().contains("Sending failed"));

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

        when(contractorOutboxRepository.findBySentFalse()).thenReturn(failedMessages);

        DealStatus closedStatus = new DealStatus("CLOSED", "Closed", true);
        Deal deal = new Deal();
        deal.setStatus(closedStatus);

        when(contractorOutboxRepository.findDealsByContractorId("123")).thenReturn(deal);

        contractorOutboxService.resendFailedMessage();

        verify(contractorOutboxRepository).save(message);
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
        DealStatus closedStatus = new DealStatus("CLOSED", "Closed", true);
        Deal deal = new Deal();
        deal.setStatus(closedStatus);

        when(contractorOutboxRepository.findDealsByContractorId(contractorId)).thenReturn(deal);

        boolean result = contractorOutboxService.shouldResend(contractorId, message);

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

        when(contractorOutboxRepository.findDealsByContractorId(contractorId)).thenReturn(deal);

        boolean result = contractorOutboxService.shouldResend(contractorId, message);

        assertTrue(result);
        assertFalse(message.isSent());
        assertThat(message.getStatus()).isEqualTo(MessageStatus.FAILED);

    }
}
