package com.fintech.deal.controller;

import com.fintech.deal.dto.ContractorDTO;
import com.fintech.deal.dto.RoleDTO;
import com.fintech.deal.exception.NotActiveException;
import com.fintech.deal.service.ContractorService;
import com.onedlvb.advice.LogLevel;
import com.onedlvb.advice.annotation.AuditLogHttp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Controller for managing roles for contractors.
 * @author Matushkin Anton
 */
@RestController
@RequestMapping("/contractor-to-role")
@RequiredArgsConstructor
@Tag(name = "Role Controller", description = "APIs for managing contractor roles")
public class RoleController {

    @NonNull
    private final ContractorService service;

    /**
     * Adds a role to a contractor.
     * @param id the ID of the contractor
     * @param roleDTO the role to add
     * @return the contractor with the added role
     */
    @PostMapping("/add/{id}")
    @AuditLogHttp(logLevel = LogLevel.INFO)
    @Operation(summary = "Add a role to a contractor", description = "Adds a role to an existing contractor", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully added role",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ContractorDTO.class))),
            @ApiResponse(responseCode = "404", description = "Contractor not found or not active",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)
    })
    public ResponseEntity<ContractorDTO> addRole(@PathVariable UUID id, @RequestBody RoleDTO roleDTO) {
        try {
            return ResponseEntity.ok(service.addRole(id, roleDTO));
        } catch (NotActiveException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Deletes a role from a contractor by deal contractor role ID.
     * @param id the ID of the deal contractor role
     * @return a response entity with the appropriate status
     */
    @DeleteMapping("/delete/{id}")
    @AuditLogHttp(logLevel = LogLevel.INFO)
    @Operation(summary = "Delete a role from a contractor",
            description = "Deletes a role from a contractor by deal contractor role ID", responses = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted role",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Role not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)
    })
    public ResponseEntity<ContractorDTO> deleteRole(@PathVariable UUID id) {
        try {
            service.deleteRoleByDealContractorRoleId(id);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

}
