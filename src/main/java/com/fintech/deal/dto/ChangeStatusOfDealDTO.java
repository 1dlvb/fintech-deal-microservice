package com.fintech.deal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fintech.deal.model.DealStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Data Transfer Object for changing the status of a deal.
 * @author Matushkin Anton
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"id", "description", "agreement_number", "agreement_date", "agreement_start_dt",
        "availability_date", "type", "status", "sum", "close_dt"})
@Schema(description = "Data Transfer Object for changing the status of a deal")
public class ChangeStatusOfDealDTO {

    @JsonProperty("id")
    @Schema(description = "Unique identifier of the deal", example = "d290f1ee-6c54-4b01-90e6-d701748f0851")
    private UUID id;

    @JsonProperty("status")
    @Schema(description = "Status of the deal", example = "ACTIVE")
    private DealStatus status;

}
