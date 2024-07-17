package com.fintech.deal.controller;

import com.fintech.deal.dto.ResponseDealDTO;
import com.fintech.deal.dto.ChangeStatusOfDealDTO;
import com.fintech.deal.dto.DealWithContractorsDTO;
import com.fintech.deal.dto.SaveOrUpdateDealDTO;
import com.fintech.deal.payload.SearchDealPayload;
import com.fintech.deal.service.DealService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public ResponseEntity<DealWithContractorsDTO> getDealById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getDealWithContractorsById(id));
    }

    @PostMapping("/search")
    public Page<DealWithContractorsDTO> searchDeals(
            SearchDealPayload payload,
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "100") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return service.searchDeals(payload, pageable);
    }

}
