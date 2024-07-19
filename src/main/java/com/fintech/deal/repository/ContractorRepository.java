package com.fintech.deal.repository;

import com.fintech.deal.model.DealContractor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContractorRepository extends JpaRepository<DealContractor, UUID> {

    @Query("SELECT COUNT(c) > 0 FROM DealContractor c WHERE c.main = true AND c.deal.id = :dealId")
    boolean existsByDealIdAndMainTrue(@Param("dealId") UUID dealId);

    @Query("SELECT c FROM DealContractor c WHERE c.deal.id = :dealId AND c.isActive = true")
    List<DealContractor> findAllByDealId(@Param("dealId") UUID dealId);

    @Query("SELECT COUNT (d) = 1 FROM Deal d JOIN d.dealContractors c WHERE c.contractorId = :contractorId AND d.isActive = true AND c.main=true ")
    boolean existsOtherDealsByContractorWhereMainIsTrueId(@Param("contractorId") String contractorId);

    @Query("SELECT COUNT(d) FROM Deal d JOIN d.dealContractors c WHERE c.contractorId = :contractorId AND d.status.id = :status AND d.isActive = true")
    Integer countDealsWithStatusActiveByContractorId(@Param("contractorId") String contractorId, @Param("status") String status);

    @Query("SELECT c FROM DealContractor c WHERE c.deal.id = :dealId AND c.contractorId = :contractorId")
    Optional<DealContractor> findByDealIdAndContractorId(@Param("dealId") UUID dealId, @Param("contractorId") String contractorId);

}
