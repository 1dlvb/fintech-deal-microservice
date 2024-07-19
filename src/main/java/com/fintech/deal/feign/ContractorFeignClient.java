package com.fintech.deal.feign;

import com.fintech.deal.dto.MainBorrowerDTO;
import com.fintech.deal.feign.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "contractor", url = "${contractor.service.url}", configuration = FeignConfig.class)
public interface ContractorFeignClient {

    @PatchMapping("/contractor/main-borrower")
    ResponseEntity<Void> updateActiveMainBorrower(@RequestBody MainBorrowerDTO dto);

}
