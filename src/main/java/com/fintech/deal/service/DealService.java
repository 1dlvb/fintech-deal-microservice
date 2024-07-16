package com.fintech.deal.service;

import com.fintech.deal.dto.ResponseDealDTO;
import com.fintech.deal.dto.ChangeStatusOfDealDTO;
import com.fintech.deal.dto.SaveOrUpdateDealDTO;
import com.fintech.deal.model.Deal;

import java.util.UUID;

public interface DealService {

    ResponseDealDTO saveDeal(SaveOrUpdateDealDTO saveOrUpdateDealDTO);

    ResponseDealDTO changeStatus(ChangeStatusOfDealDTO changeStatusOfDealDTO);

    Deal getDealById(UUID id);

}
