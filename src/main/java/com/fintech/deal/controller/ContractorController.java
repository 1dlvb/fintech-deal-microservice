package com.fintech.deal.controller;

import com.fintech.deal.dto.ContractorDTO;
import com.fintech.deal.service.ContractorService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/deal-contractor")
@RequiredArgsConstructor
public class ContractorController {

    @NonNull
    private final ContractorService service;

    @PutMapping("/save")
    public ResponseEntity<ContractorDTO> saveDeal(@RequestBody ContractorDTO contractorDTO) {
        return ResponseEntity.ok(service.saveContractor(contractorDTO));
    }

}
