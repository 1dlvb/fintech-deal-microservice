    package com.fintech.deal.dto;

    import com.fasterxml.jackson.annotation.JsonProperty;
    import com.fasterxml.jackson.annotation.JsonPropertyOrder;
    import com.fintech.deal.model.ContractorRole;
    import com.fintech.deal.model.DealContractor;
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
    public class ContractorWithNoDealIdDTO {

        @JsonProperty("id")
        private UUID id;

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

        public static ContractorWithNoDealIdDTO toDTO(DealContractor contractor) {
            return ContractorWithNoDealIdDTO.builder()
                    .id(contractor.getId())
                    .contractorId(contractor.getContractorId())
                    .name(contractor.getName())
                    .main(contractor.getMain())
                    .inn(contractor.getInn())
                    .build();
        }

    }
