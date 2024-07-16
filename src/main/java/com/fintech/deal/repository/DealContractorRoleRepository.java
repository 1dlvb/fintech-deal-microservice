package com.fintech.deal.repository;

import com.fintech.deal.model.ContractorRole;
import com.fintech.deal.model.DealContractorRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface DealContractorRoleRepository extends JpaRepository<DealContractorRole, UUID> {

    @Query("SELECT cr FROM DealContractorRole dcr " +
            "JOIN dcr.contractorRole cr " +
            "WHERE dcr.dealContractor.id = :contractorId")
    List<ContractorRole> findRolesByContractorId(@Param("contractorId") UUID contractorId);

}
