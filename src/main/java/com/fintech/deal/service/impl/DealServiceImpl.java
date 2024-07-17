package com.fintech.deal.service.impl;

import com.fintech.deal.dto.ContractorDTO;
import com.fintech.deal.dto.ContractorWithNoDealIdDTO;
import com.fintech.deal.dto.ResponseDealDTO;
import com.fintech.deal.dto.ChangeStatusOfDealDTO;
import com.fintech.deal.dto.DealWithContractorsDTO;
import com.fintech.deal.dto.SaveOrUpdateDealDTO;
import com.fintech.deal.model.ContractorRole;
import com.fintech.deal.model.Deal;
import com.fintech.deal.model.DealContractor;
import com.fintech.deal.model.DealStatus;
import com.fintech.deal.payload.SearchDealPayload;
import com.fintech.deal.repository.ContractorRepository;
import com.fintech.deal.repository.DealContractorRoleRepository;
import com.fintech.deal.repository.DealRepository;
import com.fintech.deal.repository.specification.DealSpecification;
import com.fintech.deal.service.DealService;
import com.fintech.deal.service.StatusService;
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

@Service
@RequiredArgsConstructor
public class DealServiceImpl implements DealService {

    @NonNull
    private final DealRepository repository;
    @NonNull
    private final ContractorRepository contractorRepository;
    @NonNull
    private final StatusService statusService;
    @NonNull
    private final DealContractorRoleRepository dealContractorRoleRepository;

    @Override
    public ResponseDealDTO saveDeal(SaveOrUpdateDealDTO saveOrUpdateDealDTO) {
        Deal deal = SaveOrUpdateDealDTO.fromDTO(saveOrUpdateDealDTO);
        deal.setStatus(statusService.getStatusById("DRAFT"));
        if (deal.getId() != null && repository.existsById(deal.getId())) {
            Deal existingDeal = repository.findById(deal.getId()).orElse(null);
            if (existingDeal != null) {
                updateProperties(existingDeal, deal);
                deal = repository.save(existingDeal);
            }
        } else {
            deal = repository.save(deal);
        }
        return ResponseDealDTO.toDTO(deal);
    }

    @Override
    public ResponseDealDTO changeStatus(ChangeStatusOfDealDTO changeStatusOfDealDTO) {
        UUID id = changeStatusOfDealDTO.getId();
        DealStatus status = statusService.getStatusById(changeStatusOfDealDTO.getStatus().getId());
        Optional<Deal> dealOptional = repository.findById(id);
        Deal deal = dealOptional.orElseThrow(() -> new EntityNotFoundException("Deal not found for ID: " + id));
        deal.setStatus(status);
        repository.save(deal);
        return ResponseDealDTO.toDTO(deal);
    }

    @Override
    public Deal getDealById(UUID id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public DealWithContractorsDTO getDealWithContractorsById(UUID id) {
        List<DealContractor> contractors = getListOfContractorsByDealId(id);
        return DealWithContractorsDTO.toDTO(Objects.requireNonNull(repository.findById(id).orElse(null)),
                contractors.stream().map(contractor -> getContractorDtoWithRoles(contractor, contractor.getId())).toList());
    }

    @Override
    public List<DealContractor> getListOfContractorsByDealId(UUID dealID) {
        Optional<Deal> optionalDeal = repository.findById(dealID);
        Deal deal = optionalDeal.orElse(null);
        return contractorRepository.findAllByDealId(deal.getId());
    }

    @Override
    public Page<DealWithContractorsDTO> searchDeals(SearchDealPayload payload, Pageable pageable) {
        return repository.findAll(DealSpecification.searchDeals(payload), pageable)
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
        existingDeal.setIsActive(newDealData.getIsActive());
    }

    private ContractorWithNoDealIdDTO getContractorDtoWithRoles(DealContractor contractor, UUID id) {
        ContractorWithNoDealIdDTO dto = ContractorWithNoDealIdDTO.toDTO(contractor);
        List<ContractorRole> contractorRoles = dealContractorRoleRepository.findRolesByContractorId(id);
        dto.setRoles(contractorRoles);
        return dto;
    }

}