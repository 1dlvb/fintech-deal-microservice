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
import java.util.List;
import java.util.UUID;

/**
 * Data Transfer Object representing a Deal with associated contractors.
 * @author Matushkin Anton
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"id", "description", "agreement_number", "agreement_date", "agreement_start_dt",
        "availability_date", "type", "status", "sum", "close_dt", "contractors"})
@Schema(description = "Data Transfer Object representing a Deal with associated contractors")
public class DealWithContractorsDTO {

    @JsonProperty("id")
    @Schema(description = "Unique identifier of the deal", example = "d290f1ee-6c54-4b01-90e6-d701748f0851")
    private UUID id;

    @JsonProperty("description")
    @Schema(description = "Description of the deal", example = "Construction of new office building")
    private String description;

    @JsonProperty("agreement_number")
    @Schema(description = "Agreement number of the deal", example = "123456")
    private String agreementNumber;

    @JsonProperty("agreement_date")
    @JsonFormat(pattern = "dd-MM-yyyy")
    @Schema(description = "Date of the agreement", example = "15-08-2023")
    private LocalDate agreementDate;

    @JsonProperty("agreement_start_dt")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @Schema(description = "Start date and time of the agreement", example = "15-08-2023 10:00:00")
    private LocalDateTime agreementStartDt;

    @JsonProperty("availability_date")
    @JsonFormat(pattern = "dd-MM-yyyy")
    @Schema(description = "Availability date of the deal", example = "01-09-2023")
    private LocalDate availabilityDate;

    @JsonProperty("type")
    @Schema(description = "Type of the deal", example = "CREDIT")
    private DealType type;

    @JsonProperty("status")
    @Schema(description = "Status of the deal", example = "ACTIVE")
    private DealStatus status;

    @JsonProperty("sum")
    @Schema(description = "Sum of the deal", example = "1000000.00")
    private BigDecimal sum;

    @JsonProperty("close_dt")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @Schema(description = "Close date and time of the deal", example = "30-12-2023 18:00:00")
    private LocalDateTime closeDt;

    @JsonProperty("contractors")
    @Schema(description = "List of contractors associated with the deal")
    private List<ContractorWithNoDealIdDTO> contractorDTOS;

    /**
     * Converts a DealWithContractorsDTO to a Deal entity.
     * @param responseDealDTO the DealWithContractorsDTO to convert
     * @return a Deal entity
     */
    public static Deal fromDTO(DealWithContractorsDTO responseDealDTO) {
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
     * @param contractorDTOList the list of ContractorWithNoDealIdDTO
     * @return a DealWithContractorsDTO
     */
    public static DealWithContractorsDTO toDTO(Deal deal, List<ContractorWithNoDealIdDTO> contractorDTOList) {
        return DealWithContractorsDTO.builder()
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
                .contractorDTOS(contractorDTOList)
                .build();
    }

}
