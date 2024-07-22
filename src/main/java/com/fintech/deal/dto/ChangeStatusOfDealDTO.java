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

    @JsonProperty("description")
    @Schema(description = "Description of the deal", example = "This is a sample deal description")
    private String description;

    @JsonProperty("agreement_number")
    @Schema(description = "Agreement number of the deal", example = "12345")
    private String agreementNumber;

    @JsonProperty("agreement_date")
    @JsonFormat(pattern = "dd-MM-yyyy")
    @Schema(description = "Agreement date of the deal", example = "01-01-2023")
    private LocalDate agreementDate;

    @JsonProperty("agreement_start_dt")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @Schema(description = "Agreement start date and time of the deal", example = "01-01-2023 10:00:00")
    private LocalDateTime agreementStartDt;

    @JsonProperty("availability_date")
    @JsonFormat(pattern = "dd-MM-yyyy")
    @Schema(description = "Availability date of the deal", example = "01-02-2023")
    private LocalDate availabilityDate;

    @JsonProperty("type")
    @Schema(description = "Type of the deal", example = "CREDIT")
    private DealType type;

    @JsonProperty("status")
    @Schema(description = "Status of the deal", example = "ACTIVE")
    private DealStatus status;

    @JsonProperty("sum")
    @Schema(description = "Sum of the deal", example = "10000.00")
    private BigDecimal sum;

    @JsonProperty("close_dt")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @Schema(description = "Close date and time of the deal", example = "31-12-2023 23:59:59")
    private LocalDateTime closeDt;

    /**
     * Converts a ChangeStatusOfDealDTO to a Deal entity.
     * @param changeStatusOfDealDTO the ChangeStatusOfDealDTO to convert
     * @return a Deal entity
     */
    public static Deal fromDTO(ChangeStatusOfDealDTO changeStatusOfDealDTO) {
        return Deal.builder()
                .id(changeStatusOfDealDTO.id)
                .description(changeStatusOfDealDTO.description)
                .agreementNumber(changeStatusOfDealDTO.agreementNumber)
                .agreementDate(changeStatusOfDealDTO.agreementDate)
                .agreementStartDt(changeStatusOfDealDTO.agreementStartDt)
                .availabilityDate(changeStatusOfDealDTO.availabilityDate)
                .status(changeStatusOfDealDTO.status)
                .type(changeStatusOfDealDTO.type)
                .sum(changeStatusOfDealDTO.sum)
                .closeDt(changeStatusOfDealDTO.closeDt)
                .isActive(true)
                .build();
    }

    /**
     * Converts a Deal entity to a ChangeStatusOfDealDTO.
     * @param deal the Deal entity to convert
     * @return a ChangeStatusOfDealDTO
     */
    public static ChangeStatusOfDealDTO toDTO(Deal deal) {
        return ChangeStatusOfDealDTO.builder()
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
