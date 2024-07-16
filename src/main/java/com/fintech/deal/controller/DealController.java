package com.fintech.deal.controller;

import com.fintech.deal.dto.ResponseDealDTO;
import com.fintech.deal.dto.ChangeStatusOfDealDTO;
import com.fintech.deal.dto.ResponseDealWithContractorsDTO;
import com.fintech.deal.dto.SaveOrUpdateDealDTO;
import com.fintech.deal.service.DealService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/deal")
@RequiredArgsConstructor
public class DealController {

    @NonNull
    private final DealService service;

    @PutMapping("/save")
    public ResponseEntity<ResponseDealDTO> saveDeal(@RequestBody SaveOrUpdateDealDTO saveOrUpdateDealDTO) {
        return ResponseEntity.ok(service.saveDeal(saveOrUpdateDealDTO));
    }

    @PatchMapping("/change/status")
    public ResponseEntity<ResponseDealDTO> changeStatus(@RequestBody ChangeStatusOfDealDTO statusDTO) {
        return ResponseEntity.ok(service.changeStatus(statusDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDealWithContractorsDTO> getDealById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getDealWithContractorsById(id));
    }

}
