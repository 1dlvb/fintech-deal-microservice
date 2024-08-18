package com.fintech.deal.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fintech.deal.dto.ContractorDTO;
import com.fintech.deal.dto.RoleDTO;
import com.fintech.deal.exception.NotActiveException;
import com.fintech.deal.model.ContractorRole;
import com.fintech.deal.model.DealContractor;
import com.fintech.deal.model.DealContractorRole;
import com.fintech.deal.repository.ContractorRepository;
import com.fintech.deal.repository.DealContractorRoleRepository;
import com.fintech.deal.service.ContractorOutboxService;
import com.fintech.deal.service.ContractorService;
import com.fintech.deal.service.DealService;
import com.fintech.deal.service.RoleService;
import com.fintech.deal.util.WhenUpdateMainBorrowerInvoked;
import com.onedlvb.advice.LogLevel;
import com.onedlvb.advice.annotation.AuditLog;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


/**
 * An implementation of {@link ContractorService}
 * @author Matushkin Anton
 */
@Service
@RequiredArgsConstructor
public class ContractorServiceImpl implements ContractorService {

    @NonNull
    private final ContractorRepository contractorRepository;

    @NonNull
    private final DealService dealService;

    @NonNull
    private final RoleService roleService;

    @NonNull
    private final DealContractorRoleRepository dealContractorRoleRepository;

    @NonNull
    private final ContractorOutboxService outboxService;

    @Override
    @Transactional
    @AuditLog(logLevel = LogLevel.INFO)
    public ContractorDTO saveContractor(ContractorDTO contractorDTO) {
        DealContractor contractor = ContractorDTO.fromDTO(contractorDTO, dealService);

        Optional<DealContractor> existingContractorOpt =
                contractorRepository.findByDealIdAndContractorId(contractor.getDeal().getId(), contractor.getContractorId());
        if (existingContractorOpt.isPresent()) {
            DealContractor existingContractor = existingContractorOpt.get();
            updateProperties(existingContractor, contractor);
            contractor = contractorRepository.save(existingContractor);
        } else {
            if (contractor.isMain() && contractorRepository.existsByDealIdAndMainTrue(contractor.getDeal().getId())) {
                throw new IllegalStateException("Only one record can have main = true for each deal_id");
            }
            contractor = contractorRepository.save(contractor);
        }


        return getDtoWithRoles(contractor, contractor.getId());
    }

    @Override
    @Transactional
    @AuditLog(logLevel = LogLevel.INFO)
    public void deleteContractor(UUID id) throws NotActiveException, JsonProcessingException {
        Optional<DealContractor> contractorOptional = contractorRepository.findById(id);
        DealContractor contractor = contractorOptional.orElseThrow(() ->
                new EntityNotFoundException("Contractor not found for ID: " + id));
        if (contractor.isActive()) {
            contractor.setActive(false);
            contractorRepository.save(contractor);
        } else {
            throw new NotActiveException("Contractor is not active");
        }

        updateActiveMainBorrowerInContractorService(contractor, false);
    }

    @Override
    @AuditLog(logLevel = LogLevel.INFO)
    public ContractorDTO addRole(UUID id, RoleDTO roleDTO) {
        Optional<DealContractor> contractorOptional = contractorRepository.findById(id);
        DealContractor contractor = contractorOptional.orElseThrow(() ->
                new EntityNotFoundException("Contractor not found for ID: " + id));

        ContractorRole role = roleService.findRoleById(roleDTO.getId());
        DealContractorRole dealContractorRole = new DealContractorRole();


        dealContractorRole.setDealContractor(contractor);
        dealContractorRole.setContractorRole(role);
        dealContractorRoleRepository.save(dealContractorRole);

        return getDtoWithRoles(contractor, contractor.getId());
    }

    @Override
    @AuditLog(logLevel = LogLevel.INFO)
    public void deleteRoleByDealContractorRoleId(UUID id) {
        Optional<DealContractorRole> roleOptional = dealContractorRoleRepository.findById(id);
        DealContractorRole role = roleOptional.orElse(null);
        if (role != null) {
            role.setActive(false);
            dealContractorRoleRepository.save(role);
        } else {
            throw new EntityNotFoundException("Role of contractor with ID: " + id + " not found.");
        }
    }

    @Override
    public List<DealContractor> findAllByContractorId(String id) {
        return contractorRepository.findAllByContractorId(id);
    }

    @Override
    @Transactional
    public void updateContractorByReceivedMessage(Map<String, String> contractorMap) {
        LocalDateTime messageCreationDateTime =
                LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(contractorMap.get("timestamp"))),
                ZoneId.systemDefault());
        for (DealContractor dc: findAllByContractorId(contractorMap.get("id"))) {
            dc.setName(contractorMap.get("name"));
            dc.setInn(contractorMap.get("inn"));
            if (dc.getModifyDateFromContractorMicroservice() != null &&
                    dc.getModifyDateFromContractorMicroservice().isBefore(messageCreationDateTime)) {
                dc.setModifyDateFromContractorMicroservice(messageCreationDateTime);
                saveContractor(ContractorDTO.toDTO(dc));
            }
        }
    }

    private void updateProperties(DealContractor existingContractor, DealContractor newContractorData) {
        existingContractor.setDeal(newContractorData.getDeal());
        existingContractor.setContractorId(newContractorData.getContractorId());
        existingContractor.setName(newContractorData.getName());
        existingContractor.setMain(newContractorData.isMain());
        existingContractor.setInn(newContractorData.getInn());
        existingContractor.setActive(newContractorData.isActive());
    }

    private ContractorDTO getDtoWithRoles(DealContractor contractor, UUID id) {
        ContractorDTO dto = ContractorDTO.toDTO(contractor);
        List<ContractorRole> contractorRoles = dealContractorRoleRepository.findRolesByContractorId(id);
        dto.setRoles(contractorRoles);
        return dto;
    }

    private void updateActiveMainBorrowerInContractorService(DealContractor contractor, boolean hasMainDeals) throws JsonProcessingException {
        if (contractor.isActive()
                && contractorRepository.existsOtherDealsByContractorWhereMainIsTrueId(contractor.getContractorId())) {
            outboxService.updateMainBorrower(contractor, hasMainDeals, WhenUpdateMainBorrowerInvoked.ON_DELETE);
        }
    }

}
