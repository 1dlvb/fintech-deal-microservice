package com.fintech.deal.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fintech.deal.model.DealStatus;
import com.fintech.deal.model.DealType;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Payload for searching deals.
 * This record represents the criteria used for searching deals.
 * @author Matushkin Anton
 */
@Schema(description = "Payload for searching deals based on various criteria.")
public record SearchDealPayload(
        @Schema(description = "Unique identifier of the deal.", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Description of the deal.", example = "Deal for construction project")
        String description,

        @JsonProperty("agreement_number")
        @Schema(description = "Agreement number of the deal.", example = "123456789")
        String agreementNumber,

        @JsonProperty("agreement_date_from")
        @DateTimeFormat(pattern = "dd-MM-yyyy")
        @Schema(description = "Start date for filtering deals based on agreement date.", example = "01-01-2023")
        LocalDate agreementDateFrom,

        @JsonProperty("agreement_date_to")
        @DateTimeFormat(pattern = "dd-MM-yyyy")
        @Schema(description = "End date for filtering deals based on agreement date.", example = "31-12-2023")
        LocalDate agreementDateTo,

        @JsonProperty("availability_date_from")
        @DateTimeFormat(pattern = "dd-MM-yyyy")
        @Schema(description = "Start date for filtering deals based on availability date.", example = "01-01-2023")
        LocalDate availabilityDateFrom,

        @JsonProperty("availability_date_to")
        @DateTimeFormat(pattern = "dd-MM-yyyy")
        @Schema(description = "End date for filtering deals based on availability date.", example = "31-12-2023")
        LocalDate availabilityDateTo,

        @Schema(description = "List of deal types to filter by.", example = "[\"OVERDRAFT\", \"CREDIT\"]")
        List<DealType> type,

        @Schema(description = "List of deal statuses to filter by.", example = "[\"ACTIVE\", \"CLOSED\"]")
        List<DealStatus> status,

        @JsonProperty("close_dt_from")
        @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss")
        @Schema(description = "Start date and time for filtering deals based on close date.",
                example = "01-01-2023 10:00:00")
        LocalDateTime closeDtFrom,

        @JsonProperty("close_dt_to")
        @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss")
        @Schema(description = "End date and time for filtering deals based on close date.",
                example = "31-12-2023 18:00:00")
        LocalDateTime closeDtTo,

        @Schema(description = "Search value for filtering deals based on contractor information.",
                example = "Contractor X")
        String contractorSearchValue
) {}
