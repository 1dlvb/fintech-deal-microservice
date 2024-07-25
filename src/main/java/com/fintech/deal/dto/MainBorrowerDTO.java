package com.fintech.deal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing the main borrower.
 * @author Matushkin Anton
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Data Transfer Object representing the main borrower")
public class MainBorrowerDTO {

    @JsonProperty("contractor_id")
    @Schema(description = "Identifier of the contractor", example = "d123f1ee-6c54-4b01-90e6-d701748f0123")
    private String contractorId;

    @JsonProperty("has_main_deals")
    @Schema(description = "Indicates whether the borrower has main deals", example = "true")
    private boolean hasMainDeals;

}
