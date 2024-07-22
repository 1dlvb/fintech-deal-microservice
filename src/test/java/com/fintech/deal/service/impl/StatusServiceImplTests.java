package com.fintech.deal.service.impl;

import com.fintech.deal.model.DealStatus;
import com.fintech.deal.repository.StatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StatusServiceImplTests {

    @Mock
    private StatusRepository statusRepository;

    @InjectMocks
    private StatusServiceImpl statusService;

    private DealStatus mockStatus;
    private final String statusId = "123";

    @BeforeEach
    void setUp() {
        mockStatus = new DealStatus();
        mockStatus.setId(statusId);
    }

    @Test
    void testGetStatusByIdFound() {
        when(statusRepository.findById(statusId)).thenReturn(Optional.of(mockStatus));

        DealStatus result = statusService.getStatusById(statusId);

        assertNotNull(result);
        assertEquals(statusId, result.getId());
        verify(statusRepository, times(1)).findById(statusId);
    }

    @Test
    void testGetStatusByIdNotFound() {
        when(statusRepository.findById(statusId)).thenReturn(Optional.empty());

        DealStatus result = statusService.getStatusById(statusId);

        assertNull(result);
        verify(statusRepository, times(1)).findById(statusId);
    }
}
