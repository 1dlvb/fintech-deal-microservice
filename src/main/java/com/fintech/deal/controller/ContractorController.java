package com.fintech.deal.controller;

import com.fintech.deal.dto.ContractorDTO;
import com.fintech.deal.exception.NotActiveException;
import com.fintech.deal.service.ContractorService;
import com.onedlvb.advice.LogLevel;
import com.onedlvb.advice.annotation.AuditLogHttp;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/deal-contractor")
@RequiredArgsConstructor
public class ContractorController {

    @NonNull
    private final ContractorService service;

    @PutMapping("/save")
    @AuditLogHttp(logLevel = LogLevel.INFO)
    public ResponseEntity<ContractorDTO> saveContractor(@RequestBody ContractorDTO contractorDTO) {
        return ResponseEntity.ok(service.saveContractor(contractorDTO));
    }

    @DeleteMapping("/delete/{id}")
    @AuditLogHttp(logLevel = LogLevel.INFO)
    public ResponseEntity<ContractorDTO> saveDeal(@PathVariable UUID id) {
        try {
            service.deleteContractor(id);
        } catch (NotActiveException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

}
