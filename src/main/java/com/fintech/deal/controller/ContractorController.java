package com.fintech.deal.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fintech.deal.dto.ContractorDTO;
import com.fintech.deal.exception.NotActiveException;
import com.fintech.deal.service.ContractorService;
import com.onedlvb.advice.LogLevel;
import com.onedlvb.advice.annotation.AuditLogHttp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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

/**
 * Controller for managing contractors in the deal microservice.
 * @author Matushkin Anton
 */
@RestController
@RequestMapping("/deal-contractor")
@RequiredArgsConstructor
@Tag(name = "Contractor Controller", description = "API for managing contractors")
public class ContractorController {

    @NonNull
    private final ContractorService service;

    /**
     * Saves a contractor.
     * @param contractorDTO the contractor to save
     * @return the saved contractor
     */
    @PutMapping("/save")
    @AuditLogHttp(logLevel = LogLevel.INFO)
    @Operation(summary = "Save a contractor", description = "Saves a new contractor in the system", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully saved contractor",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ContractorDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)
    })
    public ResponseEntity<ContractorDTO> saveContractor(@RequestBody ContractorDTO contractorDTO) {
        return ResponseEntity.ok(service.saveContractor(contractorDTO));
    }

    /**
     * Deletes a contractor by ID.
     * @param id the ID of the contractor to delete
     * @return a response entity with the appropriate status
     */
    @DeleteMapping("/delete/{id}")
    @AuditLogHttp(logLevel = LogLevel.INFO)
    @Operation(summary = "Delete a contractor", description = "Deletes a contractor by their ID", responses = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted contractor",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Contractor not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)
    })
    public ResponseEntity<ContractorDTO> deleteContractor(@PathVariable UUID id) {
        try {
            service.deleteContractor(id);
        } catch (NotActiveException | JsonProcessingException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

}
