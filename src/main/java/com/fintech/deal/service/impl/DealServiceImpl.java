package com.fintech.deal.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fintech.deal.dto.ContractorWithNoDealIdDTO;
import com.fintech.deal.dto.ResponseDealDTO;
import com.fintech.deal.dto.ChangeStatusOfDealDTO;
import com.fintech.deal.dto.DealWithContractorsDTO;
import com.fintech.deal.dto.SaveOrUpdateDealDTO;
import com.fintech.deal.model.ContractorRole;
import com.fintech.deal.model.Deal;
import com.fintech.deal.model.DealContractor;
import com.fintech.deal.model.DealStatus;
import com.fintech.deal.model.StatusEnum;
import com.fintech.deal.payload.SearchDealPayload;
import com.fintech.deal.repository.ContractorRepository;
import com.fintech.deal.repository.DealContractorRoleRepository;
import com.fintech.deal.repository.DealRepository;
import com.fintech.deal.repository.specification.DealSpecification;
import com.fintech.deal.service.ContractorOutboxService;
import com.fintech.deal.service.DealService;
import com.fintech.deal.service.StatusService;
import com.fintech.deal.util.WhenUpdateMainBorrowerInvoked;
import com.onedlvb.advice.LogLevel;
import com.onedlvb.advice.annotation.AuditLog;
import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;


/**
 * An implementation of {@link DealService}
 * @author Matushkin Anton
 */
@Service
@RequiredArgsConstructor
public class DealServiceImpl implements DealService {

    @NonNull
    private final DealRepository dealRepository;

    @NonNull
    private final ContractorRepository contractorRepository;

    @NonNull
    private final StatusService statusService;

    @NonNull
    private final DealContractorRoleRepository dealContractorRoleRepository;

    @NonNull
    private final ContractorOutboxService outboxService;

    @Override
    @AuditLog(logLevel = LogLevel.INFO)
    public ResponseDealDTO saveDeal(SaveOrUpdateDealDTO saveOrUpdateDealDTO) {
        Deal deal = SaveOrUpdateDealDTO.fromDTO(saveOrUpdateDealDTO);
        deal.setStatus(statusService.getStatusById(StatusEnum.DRAFT.name()));
        if (deal.getId() != null && dealRepository.existsById(deal.getId())) {
            Deal existingDeal = dealRepository.findById(deal.getId()).orElse(null);
            if (existingDeal != null) {
                updateProperties(existingDeal, deal);
                deal = dealRepository.save(existingDeal);
            }
        } else {
            deal = dealRepository.save(deal);
        }
        return ResponseDealDTO.toDTO(deal);
    }

    @Override
    @AuditLog(logLevel = LogLevel.INFO)
    public ResponseDealDTO changeStatus(ChangeStatusOfDealDTO changeStatusOfDealDTO) throws JsonProcessingException {
        UUID id = changeStatusOfDealDTO.getId();

        DealStatus status = statusService.getStatusById(changeStatusOfDealDTO.getStatus().getId());
        Optional<Deal> dealOptional = dealRepository.findById(id);
        Deal deal = dealOptional.orElseThrow(() -> new EntityNotFoundException("Deal not found for ID: " + id));

        String dealOldStatus = deal.getStatus().getId();
        deal.setStatus(status);
        dealRepository.save(deal);
        List<DealContractor> dealContractors = contractorRepository.findAllByDealId(deal.getId());
        for (DealContractor dc: dealContractors) {
            if (dealOldStatus.equals(StatusEnum.DRAFT.name()) && status.getId().equals(StatusEnum.ACTIVE.name())) {
                updateActiveMainBorrowerInContractorService(dc, StatusEnum.ACTIVE.name(), true);
            } else if (dealOldStatus.equals(StatusEnum.ACTIVE.name())
                    && status.getId().equals(StatusEnum.CLOSED.name())) {
                updateActiveMainBorrowerInContractorService(dc, StatusEnum.CLOSED.name(), false);
            }
        }

        return ResponseDealDTO.toDTO(deal);
    }

    @Override
    @AuditLog(logLevel = LogLevel.INFO)
    public Deal getDealById(UUID id) {
        return dealRepository.findById(id).orElse(null);
    }

    @Override
    @AuditLog(logLevel = LogLevel.INFO)
    public DealWithContractorsDTO getDealWithContractorsById(UUID id) {
        List<DealContractor> contractors = getListOfContractorsByDealId(id);
        return DealWithContractorsDTO.toDTO(Objects.requireNonNull(dealRepository.findById(id).orElse(null)),
                contractors.stream().map(contractor -> getContractorDtoWithRoles(contractor, contractor.getId())).toList());
    }

    @Override
    @AuditLog(logLevel = LogLevel.INFO)
    public List<DealContractor> getListOfContractorsByDealId(UUID dealID) {
        Optional<Deal> optionalDeal = dealRepository.findById(dealID);
        Deal deal = optionalDeal.orElse(null);
        return contractorRepository.findAllByDealId(Objects.requireNonNull(deal).getId());
    }

    @Override
    @AuditLog(logLevel = LogLevel.INFO)
    public Page<DealWithContractorsDTO> searchDeals(SearchDealPayload payload, Pageable pageable) {
        return dealRepository.findAll(DealSpecification.searchDeals(payload), pageable)
                .map(deal -> DealWithContractorsDTO.toDTO(deal, getListOfContractorsByDealId(deal.getId()).stream()
                        .map(contractor -> {
                            ContractorWithNoDealIdDTO dto = getContractorDtoWithRoles(contractor, contractor.getId());
                            if (!dto.getRoles().isEmpty()) {
                                return dto;
                            }
                            return null;
                        })
                        .filter(Objects::nonNull)
                        .toList()));
    }

    private void updateProperties(Deal existingDeal, Deal newDealData) {
        existingDeal.setDescription(newDealData.getDescription());
        existingDeal.setAgreementNumber(newDealData.getAgreementNumber());
        existingDeal.setAgreementDate(newDealData.getAgreementDate());
        existingDeal.setAgreementStartDt(newDealData.getAgreementStartDt());
        existingDeal.setAvailabilityDate(newDealData.getAvailabilityDate());
        existingDeal.setType(newDealData.getType());
        existingDeal.setStatus(newDealData.getStatus());
        existingDeal.setSum(newDealData.getSum());
        existingDeal.setCloseDt(newDealData.getCloseDt());
        existingDeal.setActive(newDealData.isActive());
    }

    private ContractorWithNoDealIdDTO getContractorDtoWithRoles(DealContractor contractor, UUID id) {
        ContractorWithNoDealIdDTO dto = ContractorWithNoDealIdDTO.toDTO(contractor);
        List<ContractorRole> contractorRoles = dealContractorRoleRepository.findRolesByContractorId(id);
        dto.setRoles(contractorRoles);
        return dto;
    }

    private void updateActiveMainBorrowerInContractorService(DealContractor dc, String status, boolean hasMainDeals) throws JsonProcessingException {
        if (contractorRepository.countDealsWithStatusActiveByContractorId(dc.getContractorId(), status) == 1
                && dc.isMain()) {
            if (status.equals(StatusEnum.ACTIVE.name())) {
                outboxService.updateMainBorrower(dc, hasMainDeals, WhenUpdateMainBorrowerInvoked.ON_UPDATE_STATUS_ACTIVE);
            } else if (status.equals(StatusEnum.CLOSED.name())) {
                outboxService.updateMainBorrower(dc, hasMainDeals, WhenUpdateMainBorrowerInvoked.ON_UPDATE_STATUS_CLOSED);
            }
        }
    }

}
