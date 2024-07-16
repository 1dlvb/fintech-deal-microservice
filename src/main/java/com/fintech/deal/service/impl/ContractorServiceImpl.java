package com.fintech.deal.service.impl;

import com.fintech.deal.dto.ContractorDTO;
import com.fintech.deal.model.DealContractor;
import com.fintech.deal.repository.ContractorRepository;
import com.fintech.deal.service.ContractorService;
import com.fintech.deal.service.DealService;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContractorServiceImpl implements ContractorService {

    @NonNull
    private final ContractorRepository repository;

    @NonNull
    private final DealService dealService;

    @Override
    @Transactional
    public ContractorDTO saveContractor(ContractorDTO contractorDTO) {
        DealContractor contractor = ContractorDTO.fromDTO(contractorDTO, dealService);
        if (contractor.getId() != null && repository.existsById(contractor.getId())) {
            DealContractor existingContractor = repository.findById(contractor.getId()).orElse(null);
            if (existingContractor != null) {
//                if (existingContractor.getMain()) {
//                    UUID dealId = contractor.getDeal().getId();
//                    if (repository.existsByDealIdAndMainTrue(dealId, contractor.getId())) {
//                        throw new IllegalStateException("There can only be one main contractor per deal.");
//                    }
//                    updateProperties(existingContractor, contractor);
//                    contractor = repository.save(existingContractor);
//                }
            }

        } else {
            contractor = repository.save(contractor);
        }
        return ContractorDTO.toDTO(contractor);
    }

    private void updateProperties(DealContractor existingContractor, DealContractor newContractorData) {
        existingContractor.setDeal(newContractorData.getDeal());
        existingContractor.setContractorId(newContractorData.getContractorId());
        existingContractor.setName(newContractorData.getName());
        existingContractor.setMain(newContractorData.getMain());
        existingContractor.setInn(newContractorData.getInn());
        existingContractor.setIsActive(newContractorData.getIsActive());
    }

}
