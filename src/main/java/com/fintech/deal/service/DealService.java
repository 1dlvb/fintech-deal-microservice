package com.fintech.deal.service;

import com.fintech.deal.dto.ResponseDealDTO;
import com.fintech.deal.dto.ChangeStatusOfDealDTO;
import com.fintech.deal.dto.DealWithContractorsDTO;
import com.fintech.deal.dto.SaveOrUpdateDealDTO;
import com.fintech.deal.model.Deal;
import com.fintech.deal.model.DealContractor;
import com.fintech.deal.payload.SearchDealPayload;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface DealService {

    ResponseDealDTO saveDeal(SaveOrUpdateDealDTO saveOrUpdateDealDTO);

    ResponseDealDTO changeStatus(ChangeStatusOfDealDTO changeStatusOfDealDTO);

    Deal getDealById(UUID id);

    DealWithContractorsDTO getDealWithContractorsById(UUID id);

    List<DealContractor> getListOfContractorsByDealId(UUID dealID);

    Page<DealWithContractorsDTO> searchDeals(SearchDealPayload payload, Pageable pageable);

}
