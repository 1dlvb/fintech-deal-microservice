package com.fintech.deal.repository;

import com.fintech.deal.model.DealContractor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ContractorRepository extends JpaRepository<DealContractor, UUID> {

    @Query("SELECT COUNT(dc) > 0 FROM DealContractor dc WHERE dc.main = true AND dc.deal.id = :dealId")
    boolean existsByDealIdAndMainTrue(@Param("dealId") UUID dealId);

}
