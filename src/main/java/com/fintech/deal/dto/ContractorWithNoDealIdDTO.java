package com.fintech.deal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fintech.deal.model.ContractorRole;
import com.fintech.deal.model.DealContractor;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Data Transfer Object representing a contractor without deal ID.
 * @author Matushkin Anton
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"id", "contractor_id", "name", "inn"})
@Schema(description = "Data Transfer Object representing a contractor without deal ID")
public class ContractorWithNoDealIdDTO {

    @JsonProperty("id")
    @Schema(description = "Unique identifier of the contractor", example = "d290f1ee-6c54-4b01-90e6-d701748f0851")
    private UUID id;

    @JsonProperty("contractor_id")
    @Schema(description = "Contractor's identifier", example = "d123f1ee-6c54-4b01-90e6-d701748f1234")
    private String contractorId;

    @JsonProperty("inn")
    @Schema(description = "Contractor's tax identification number", example = "1234567890")
    private String inn;

    @JsonProperty("name")
    @Schema(description = "Name of the contractor", example = "My name")
    private String name;

    @JsonProperty(value = "main", defaultValue = "false")
    @Schema(description = "Flag indicating if the contractor is the main contractor", example = "true")
    private Boolean main;

    @JsonProperty(value = "roles")
    @Schema(description = "List of roles assigned to the contractor")
    private List<ContractorRole> roles;

    /**
     * Converts a DealContractor entity to a ContractorWithNoDealIdDTO.
     * @param contractor the DealContractor entity to convert
     * @return a ContractorWithNoDealIdDTO
     */
    public static ContractorWithNoDealIdDTO toDTO(DealContractor contractor) {
        return ContractorWithNoDealIdDTO.builder()
                .id(contractor.getId())
                .contractorId(contractor.getContractorId())
                .name(contractor.getName())
                .main(contractor.isMain())
                .inn(contractor.getInn())
                .build();
    }

}
