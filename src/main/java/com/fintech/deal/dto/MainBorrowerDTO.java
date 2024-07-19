package com.fintech.deal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MainBorrowerDTO {

    @JsonProperty("contractor_id")
    private String contractorId;

    @JsonProperty("has_main_deals")
    private boolean hasMainDeals;

}
