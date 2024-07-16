    package com.fintech.deal.dto;

    import com.fasterxml.jackson.annotation.JsonProperty;
    import com.fasterxml.jackson.annotation.JsonPropertyOrder;
    import com.fintech.deal.model.ContractorRole;
    import com.fintech.deal.model.DealContractor;
    import com.fintech.deal.service.DealService;
    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    import java.util.List;
    import java.util.UUID;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonPropertyOrder({"id", "deal_id", "contractor_id", "name", "inn"})
    public class ContractorDTO {

        @JsonProperty("id")
        private UUID id;

        @JsonProperty("deal_id")
        private UUID dealId;

        @JsonProperty("contractor_id")
        private String contractorId;

        @JsonProperty("inn")
        private String inn;

        @JsonProperty("name")
        private String name;

        @JsonProperty(value = "main", defaultValue = "false")
        private Boolean main;

        @JsonProperty(value = "roles")
        private List<ContractorRole> roles;

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
