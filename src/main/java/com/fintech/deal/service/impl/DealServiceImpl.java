package com.fintech.deal.service.impl;

import com.fintech.deal.dto.ContractorDTO;
import com.fintech.deal.dto.ResponseDealDTO;
import com.fintech.deal.dto.ChangeStatusOfDealDTO;
import com.fintech.deal.dto.ResponseDealWithContractorsDTO;
import com.fintech.deal.dto.SaveOrUpdateDealDTO;
import com.fintech.deal.model.Deal;
import com.fintech.deal.model.DealContractor;
import com.fintech.deal.model.DealStatus;
import com.fintech.deal.repository.ContractorRepository;
import com.fintech.deal.repository.DealRepository;
import com.fintech.deal.service.DealService;
import com.fintech.deal.service.StatusService;
import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
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
    public ResponseDealWithContractorsDTO getDealWithContractorsById(UUID id) {
        Optional<Deal> optionalDeal = repository.findById(id);
        Deal deal = optionalDeal.orElse(null);
        List<DealContractor> contractors = contractorRepository.findAllByDealId(deal.getId());
        return ResponseDealWithContractorsDTO.toDTO(deal,
                contractors.stream().map(ContractorDTO::toDTO).toList());
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

}
