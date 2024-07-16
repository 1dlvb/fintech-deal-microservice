package com.fintech.deal.repository;

import com.fintech.deal.model.DealContractor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ContractorRepository extends JpaRepository<DealContractor, UUID> {

    @Query("SELECT COUNT(c) > 0 FROM DealContractor c WHERE c.main = true AND c.deal.id = :dealId")
    boolean existsByDealIdAndMainTrue(@Param("dealId") UUID dealId);

    @Query("SELECT c FROM DealContractor c WHERE c.deal.id = :dealId")
    List<DealContractor> findAllByDealId(@Param("dealId") UUID dealId);

}
