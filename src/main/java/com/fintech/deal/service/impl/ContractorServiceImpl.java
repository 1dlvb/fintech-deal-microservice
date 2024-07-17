package com.fintech.deal.service.impl;

import com.fintech.deal.dto.ContractorDTO;
import com.fintech.deal.dto.RoleDTO;
import com.fintech.deal.exception.NotActiveException;
import com.fintech.deal.model.ContractorRole;
import com.fintech.deal.model.DealContractor;
import com.fintech.deal.model.DealContractorRole;
import com.fintech.deal.repository.ContractorRepository;
import com.fintech.deal.repository.DealContractorRoleRepository;
import com.fintech.deal.service.ContractorService;
import com.fintech.deal.service.DealService;
import com.fintech.deal.service.RoleService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContractorServiceImpl implements ContractorService {

    @NonNull
    private final ContractorRepository repository;

    @NonNull
    private final DealService dealService;

    @NonNull
    private final RoleService roleService;

    @NonNull
    private final DealContractorRoleRepository dealContractorRoleRepository;

    @Override
    @Transactional
    public ContractorDTO saveContractor(ContractorDTO contractorDTO) {

        DealContractor contractor = ContractorDTO.fromDTO(contractorDTO, dealService);
        if (contractor.getMain() && repository.existsByDealIdAndMainTrue(contractor.getDeal().getId())) {
            throw new IllegalStateException("Only one record can have main = true for each deal_id");
        }
        if (contractor.getId() != null && repository.existsById(contractor.getId())) {
            DealContractor existingContractor = repository.findById(contractor.getId()).orElse(null);
            if (existingContractor != null) {
                    updateProperties(existingContractor, contractor);
                    contractor = repository.save(existingContractor);
            }

        } else {
            contractor = repository.save(contractor);
        }
        return getDtoWithRoles(contractor, contractor.getId());
    }

    @Override
    @Transactional
    public void deleteContractor(UUID id) throws NotActiveException {
        Optional<DealContractor> contractorOptional = repository.findById(id);
        DealContractor contractor = contractorOptional.orElseThrow(() ->
                new EntityNotFoundException("Contractor not found for ID: " + id));
        if (contractor.getIsActive()) {
            contractor.setIsActive(false);
            repository.save(contractor);
        } else {
            throw new NotActiveException("Contractor is not active");
        }
    }

    @Override
    public ContractorDTO addRole(UUID id, RoleDTO roleDTO) {
        Optional<DealContractor> contractorOptional = repository.findById(id);
        DealContractor contractor = contractorOptional.orElseThrow(() ->
                new EntityNotFoundException("Contractor not found for ID: " + id));

        ContractorRole role = roleService.findRoleById(roleDTO.getId());
        DealContractorRole dealContractorRole = new DealContractorRole();


        dealContractorRole.setDealContractor(contractor);
        dealContractorRole.setContractorRole(role);
        dealContractorRoleRepository.save(dealContractorRole);

        return getDtoWithRoles(contractor, id);
    }

    @Override
    public void deleteRoleByDealContractorRoleId(UUID id) {
        Optional<DealContractorRole> roleOptional = dealContractorRoleRepository.findById(id);
        DealContractorRole role = roleOptional.orElse(null);
        if (role != null) {
            role.setIsActive(false);
            dealContractorRoleRepository.save(role);
        } else {
            throw new EntityNotFoundException("Role of contractor with ID: " + id + " not found.");
        }
    }

    private void updateProperties(DealContractor existingContractor, DealContractor newContractorData) {
        existingContractor.setDeal(newContractorData.getDeal());
        existingContractor.setContractorId(newContractorData.getContractorId());
        existingContractor.setName(newContractorData.getName());
        existingContractor.setMain(newContractorData.getMain());
        existingContractor.setInn(newContractorData.getInn());
        existingContractor.setIsActive(newContractorData.getIsActive());
    }

    private ContractorDTO getDtoWithRoles(DealContractor contractor, UUID id) {
        ContractorDTO dto = ContractorDTO.toDTO(contractor);
        List<ContractorRole> contractorRoles = dealContractorRoleRepository.findRolesByContractorId(id);
        dto.setRoles(contractorRoles);
        return dto;
    }

}
