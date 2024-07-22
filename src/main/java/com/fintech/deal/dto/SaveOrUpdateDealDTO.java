package com.fintech.deal.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fintech.deal.model.Deal;
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
 * Data Transfer Object for saving or updating a deal.
 * @author Matushkin Anton
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"id", "description", "agreement_number", "agreement_date", "agreement_start_dt",
        "availability_date", "type", "sum", "close_dt"})
@Schema(description = "Data Transfer Object for saving or updating a deal")
public class SaveOrUpdateDealDTO {

    @JsonProperty("id")
    @Schema(description = "Unique identifier of the deal", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @JsonProperty("description")
    @Schema(description = "Description of the deal", example = "Deal for office renovation")
    private String description;

    @JsonProperty("agreement_number")
    @Schema(description = "Number of the agreement", example = "AG-2024-001")
    private String agreementNumber;

    @JsonProperty("agreement_date")
    @JsonFormat(pattern = "dd-MM-yyyy")
    @Schema(description = "Date of the agreement", example = "01-01-2024")
    private LocalDate agreementDate;

    @JsonProperty("agreement_start_dt")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @Schema(description = "Start date and time of the agreement", example = "01-01-2024 10:00:00")
    private LocalDateTime agreementStartDt;

    @JsonProperty("availability_date")
    @JsonFormat(pattern = "dd-MM-yyyy")
    @Schema(description = "Date when the deal becomes available", example = "15-01-2024")
    private LocalDate availabilityDate;

    @JsonProperty("type")
    @Schema(description = "Type of the deal", example = "CREDIT")
    private DealType type;

    @JsonProperty("sum")
    @Schema(description = "Total sum of the deal", example = "15000.00")
    private BigDecimal sum;

    @JsonProperty("close_dt")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @Schema(description = "Date and time when the deal is closed", example = "30-06-2024 18:00:00")
    private LocalDateTime closeDt;

    /**
     * Converts a SaveOrUpdateDealDTO to a Deal entity.
     * @param saveOrUpdateDealDTO the DTO to convert
     * @return the Deal entity
     */
    public static Deal fromDTO(SaveOrUpdateDealDTO saveOrUpdateDealDTO) {
        return Deal.builder()
                .id(saveOrUpdateDealDTO.id)
                .description(saveOrUpdateDealDTO.description)
                .agreementNumber(saveOrUpdateDealDTO.agreementNumber)
                .agreementDate(saveOrUpdateDealDTO.agreementDate)
                .agreementStartDt(saveOrUpdateDealDTO.agreementStartDt)
                .availabilityDate(saveOrUpdateDealDTO.availabilityDate)
                .type(saveOrUpdateDealDTO.type)
                .sum(saveOrUpdateDealDTO.sum)
                .closeDt(saveOrUpdateDealDTO.closeDt)
                .isActive(true)
                .build();
    }

    /**
     * Converts a Deal entity to a SaveOrUpdateDealDTO.
     * @param deal the Deal entity to convert
     * @return the DTO
     */
    public static SaveOrUpdateDealDTO toDTO(Deal deal) {
        return SaveOrUpdateDealDTO.builder()
                .id(deal.getId())
                .description(deal.getDescription())
                .agreementNumber(deal.getAgreementNumber())
                .agreementDate(deal.getAgreementDate())
                .agreementStartDt(deal.getAgreementStartDt())
                .availabilityDate(deal.getAvailabilityDate())
                .type(deal.getType())
                .sum(deal.getSum())
                .closeDt(deal.getCloseDt())
                .build();
    }

}
