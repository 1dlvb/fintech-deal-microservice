package com.fintech.deal.service.impl;

import com.fintech.deal.config.DealConfig;
import com.fintech.deal.model.ContractorRole;
import com.fintech.deal.quartz.config.QuartzConfig;
import com.fintech.deal.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@Import({DealConfig.class, QuartzConfig.class})
class RoleServiceImplTests {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    private ContractorRole mockRole;
    private final String roleId = "123";

    @BeforeEach
    void setUp() {
        mockRole = new ContractorRole();
        mockRole.setId(roleId);
    }

    @Test
    void testFindRoleByIdFound() {
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(mockRole));

        ContractorRole result = roleService.findRoleById(roleId);

        assertNotNull(result);
        assertEquals(roleId, result.getId());
        verify(roleRepository, times(1)).findById(roleId);
    }

    @Test
    void testFindRoleByIdNotFound() {
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        ContractorRole result = roleService.findRoleById(roleId);

        assertNull(result);
        verify(roleRepository, times(1)).findById(roleId);
    }
}
