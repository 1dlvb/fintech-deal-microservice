package com.fintech.deal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fintech.deal.model.ContractorRole;
import com.fintech.deal.service.RoleService;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Role.
 * @author Matushkin Anton
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"id"})
@Schema(description = "Data Transfer Object for Role")
public class RoleDTO {

    @JsonProperty("id")
    @Schema(description = "Unique identifier of the role", example = "DRAWER")
    private String id;

    /**
     * Converts a RoleDTO to a ContractorRole entity.
     * @param roleDTO the RoleDTO to convert
     * @param roleService the RoleService used to fetch the role details
     * @return the ContractorRole entity
     */
    public static ContractorRole fromDTO(RoleDTO roleDTO, RoleService roleService) {
        ContractorRole role = roleService.findRoleById(roleDTO.id);
        return ContractorRole.builder()
                .id(roleDTO.id)
                .name(role.getName())
                .category(role.getCategory())
                .isActive(true)
                .build();
    }

    /**
     * Converts a ContractorRole entity to a RoleDTO.
     * @param role the ContractorRole entity to convert
     * @return the RoleDTO
     */
    public static RoleDTO toDTO(ContractorRole role) {
        return RoleDTO.builder()
                .id(role.getId())
                .build();
    }

}
