package com.fintech.deal.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fintech.deal.model.DealStatus;
import com.fintech.deal.model.DealType;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record SearchDealPayload(
    UUID id,
    String description,
    @JsonProperty("agreement_number")
    String agreementNumber,
    @JsonProperty("agreement_date_from")
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    LocalDate agreementDateFrom,
    @JsonProperty("agreement_date_to")
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    LocalDate agreementDateTo,
    @JsonProperty("availability_date_from")
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    LocalDate availabilityDateFrom,
    @JsonProperty("availability_date_to")
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    LocalDate availabilityDateTo,
    List<DealType> type,
    List<DealStatus> status,
    @JsonProperty("close_dt_from")
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    LocalDateTime closeDtFrom,
    @JsonProperty("close_dt_to")
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    LocalDateTime closeDtTo,
    String contractorSearchValue

){}
