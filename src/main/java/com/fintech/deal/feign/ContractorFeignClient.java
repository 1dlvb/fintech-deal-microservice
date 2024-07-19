package com.fintech.deal.feign;

import com.fintech.deal.dto.MainBorrowerDTO;
import com.fintech.deal.feign.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "contractor", url = "localhost:8080", configuration = FeignConfig.class)
public interface ContractorFeignClient {

    @PatchMapping("/contractor/main-borrower")
    String updateActiveMainBorrower(@RequestBody MainBorrowerDTO dto);

}
