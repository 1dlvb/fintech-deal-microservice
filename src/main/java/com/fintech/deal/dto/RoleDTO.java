package com.fintech.deal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fintech.deal.model.ContractorRole;
import com.fintech.deal.service.RoleService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"id"})
public class RoleDTO {

    @JsonProperty("id")
    private String id;
    public static ContractorRole fromDTO(RoleDTO roleDTO, RoleService roleService) {
        ContractorRole role = roleService.findRoleById(roleDTO.id);
        return ContractorRole.builder()
                .id(roleDTO.id)
                .name(role.getName())
                .category(role.getCategory())
                .isActive(true)
                .build();
    }

    public static RoleDTO toDTO(ContractorRole role) {
        return RoleDTO.builder()
                .id(role.getId())
                .build();
    }

}
