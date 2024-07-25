package com.fintech.deal.controller;

import com.fintech.deal.dto.ResponseDealDTO;
import com.fintech.deal.dto.ChangeStatusOfDealDTO;
import com.fintech.deal.dto.DealWithContractorsDTO;
import com.fintech.deal.dto.SaveOrUpdateDealDTO;
import com.fintech.deal.payload.SearchDealPayload;
import com.fintech.deal.service.DealService;
import com.onedlvb.advice.LogLevel;
import com.onedlvb.advice.annotation.AuditLogHttp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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

/**
 * Controller for managing deals in the deal system.
 * @author Matushkin Anton
 */
@RestController
@RequestMapping("/deal")
@RequiredArgsConstructor
@Tag(name = "Deal Controller", description = "APIs for managing deals")
public class DealController {

    @NonNull
    private final DealService service;

    /**
     * Saves a deal.
     * @param saveOrUpdateDealDTO the deal to save or update
     * @return the saved or updated deal
     */
    @PutMapping("/save")
    @AuditLogHttp(logLevel = LogLevel.INFO)
    @Operation(summary = "Save a deal", description = "Saves a new deal in the system", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully saved deal",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDealDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)
    })
    public ResponseEntity<ResponseDealDTO> saveDeal(@RequestBody SaveOrUpdateDealDTO saveOrUpdateDealDTO) {
        return ResponseEntity.ok(service.saveDeal(saveOrUpdateDealDTO));
    }

    /**
     * Changes the status of a deal.
     * When status is changed from DRAFT to ACTIVE, the microservice sends request to
     * Contractor microservice and sets active_main_borrower to true.
     * When status is changed from ACTIVE to CLOSED, the microservice sends request to
     * Contractor microservice and sets active_main_borrower to false.
     * @param statusDTO the status change data
     * @return the deal with the updated status
     */
    @PatchMapping("/change/status")
    @AuditLogHttp(logLevel = LogLevel.INFO)
    @Operation(summary = "Change deal status", description = "Changes the status of an existing deal", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully changed status",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDealDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)
    })
    public ResponseEntity<ResponseDealDTO> changeStatus(@RequestBody ChangeStatusOfDealDTO statusDTO) {
        return ResponseEntity.ok(service.changeStatus(statusDTO));
    }

    /**
     * Gets a deal by ID.
     * @param id the ID of the deal to retrieve
     * @return the deal with its contractors
     */
    @GetMapping("/{id}")
    @AuditLogHttp(logLevel = LogLevel.INFO)
    @Operation(summary = "Get deal by ID", description = "Retrieves a deal by its ID", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved deal",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DealWithContractorsDTO.class))),
            @ApiResponse(responseCode = "404", description = "Deal not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)
    })
    public ResponseEntity<DealWithContractorsDTO> getDealById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getDealWithContractorsById(id));
    }

    /**
     * Searches for deals based on criteria.
     * @param payload the search criteria
     * @param page the page number to retrieve
     * @param size the number of items per page
     * @return a page of deals matching the criteria
     */
    @PostMapping("/search")
    @AuditLogHttp(logLevel = LogLevel.INFO)
    @Operation(summary = "Search deals", description = "Searches for deals based on criteria", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved deals",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "Invalid search criteria",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)
    })
    public Page<DealWithContractorsDTO> searchDeals(
            @Parameter(description = "Search criteria") @RequestBody SearchDealPayload payload,
            @Parameter(description = "Page number") @RequestParam(name = "page", defaultValue = "0") Integer page,
            @Parameter(description = "Page size") @RequestParam(name = "size", defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return service.searchDeals(payload, pageable);
    }

}
