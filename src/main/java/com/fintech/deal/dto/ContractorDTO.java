package com.fintech.deal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fintech.deal.model.ContractorRole;
import com.fintech.deal.model.DealContractor;
import com.fintech.deal.service.DealService;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Data Transfer Object for Contractor.
 * @author Matushkin Anton
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"id", "deal_id", "contractor_id", "name", "inn"})
@Schema(description = "Data Transfer Object representing a contractor")
public class ContractorDTO {

    @JsonProperty("id")
    @Schema(description = "Unique identifier of the contractor",
            example = "d290f1ee-6c54-4b01-90e6-d701748f0851")
    private UUID id;

    @JsonProperty("deal_id")
    @Schema(description = "Unique identifier of the deal associated with the contractor",
            example = "d290f1ee-6c54-4b01-90e6-d701748f0852")
    private UUID dealId;

    @JsonProperty("contractor_id")
    @Schema(description = "Contractor's identifier", example = "d510f1ee-2c54-4b01-90e6-d701748f0123")
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
     * Converts a ContractorDTO to a DealContractor entity.
     * @param contractorDTO the ContractorDTO to convert
     * @param dealService the DealService to use for deal lookups
     * @return a DealContractor entity
     */
    public static DealContractor fromDTO(ContractorDTO contractorDTO, DealService dealService) {
        return DealContractor.builder()
                .id(contractorDTO.id)
                .deal(dealService.getDealById(contractorDTO.dealId))
                .contractorId(contractorDTO.contractorId)
                .name(contractorDTO.name)
                .main(contractorDTO.getMain() != null ? contractorDTO.getMain() : false)
                .inn(contractorDTO.inn)
                .isActive(true)
                .build();
    }

    /**
     * Converts a DealContractor entity to a ContractorDTO.
     * @param contractor the DealContractor entity to convert
     * @return a ContractorDTO
     */
    public static ContractorDTO toDTO(DealContractor contractor) {
        return ContractorDTO.builder()
                .id(contractor.getId())
                .dealId(contractor.getDeal().getId())
                .contractorId(contractor.getContractorId())
                .name(contractor.getName())
                .main(contractor.getMain())
                .inn(contractor.getInn())
                .build();
    }

}
