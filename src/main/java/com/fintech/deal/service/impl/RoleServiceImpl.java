package com.fintech.deal.service.impl;

import com.fintech.deal.model.ContractorRole;
import com.fintech.deal.repository.RoleRepository;
import com.fintech.deal.service.RoleService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    @NonNull
    private final RoleRepository roleRepository;

    @Override
    public ContractorRole findRoleById(String id) {
        return roleRepository.findById(id).orElse(null);
    }

}
