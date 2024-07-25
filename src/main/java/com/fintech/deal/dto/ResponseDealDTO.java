package com.fintech.deal.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fintech.deal.model.Deal;
import com.fintech.deal.model.DealStatus;
import com.fintech.deal.model.DealType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object for Deal response.
 * @author Matushkin Anton
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"id", "description", "agreement_number", "agreement_date", "agreement_start_dt",
        "availability_date", "type", "status", "sum", "close_dt"})
@Schema(description = "Data Transfer Object for Deal response")
public class ResponseDealDTO {

    @JsonProperty("id")
    @Schema(description = "Unique identifier of the deal", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @JsonProperty("description")
    @Schema(description = "Description of the deal", example = "Deal for project")
    private String description;

    @JsonProperty("agreement_number")
    @Schema(description = "Agreement number of the deal", example = "123456")
    private String agreementNumber;

    @JsonProperty("agreement_date")
    @JsonFormat(pattern = "dd-MM-yyyy")
    @Schema(description = "Agreement date of the deal", example = "15-07-2023")
    private LocalDate agreementDate;

    @JsonProperty("agreement_start_dt")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @Schema(description = "Agreement start date and time of the deal", example = "15-07-2023 10:00:00")
    private LocalDateTime agreementStartDt;

    @JsonProperty("availability_date")
    @JsonFormat(pattern = "dd-MM-yyyy")
    @Schema(description = "Availability date of the deal", example = "20-07-2023")
    private LocalDate availabilityDate;

    @JsonProperty("type")
    @Schema(description = "Type of the deal", example = "CREDIT")
    private DealType type;

    @JsonProperty("status")
    @Schema(description = "Status of the deal", example = "ACTIVE")
    private DealStatus status;

    @JsonProperty("sum")
    @Schema(description = "Sum of the deal", example = "100000.00")
    private BigDecimal sum;

    @JsonProperty("close_dt")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @Schema(description = "Close date and time of the deal", example = "30-12-2023 15:00:00")
    private LocalDateTime closeDt;

    /**
     * Converts a ResponseDealDTO to a Deal entity.
     * @param responseDealDTO the ResponseDealDTO to convert
     * @return a Deal entity
     */
    public static Deal fromDTO(ResponseDealDTO responseDealDTO) {
        return Deal.builder()
                .id(responseDealDTO.id)
                .description(responseDealDTO.description)
                .agreementNumber(responseDealDTO.agreementNumber)
                .agreementDate(responseDealDTO.agreementDate)
                .agreementStartDt(responseDealDTO.agreementStartDt)
                .availabilityDate(responseDealDTO.availabilityDate)
                .type(responseDealDTO.type)
                .status(responseDealDTO.status)
                .sum(responseDealDTO.sum)
                .closeDt(responseDealDTO.closeDt)
                .isActive(true)
                .build();
    }

    /**
     * Converts a Deal entity and a list of ContractorWithNoDealIdDTO to a DealWithContractorsDTO.
     * @param deal the Deal entity
     * @return a ResponseDealDTO
     */
    public static ResponseDealDTO toDTO(Deal deal) {
        return ResponseDealDTO.builder()
                .id(deal.getId())
                .description(deal.getDescription())
                .agreementNumber(deal.getAgreementNumber())
                .agreementDate(deal.getAgreementDate())
                .agreementStartDt(deal.getAgreementStartDt())
                .availabilityDate(deal.getAvailabilityDate())
                .type(deal.getType())
                .status(deal.getStatus())
                .sum(deal.getSum())
                .closeDt(deal.getCloseDt())
                .build();
    }

}
