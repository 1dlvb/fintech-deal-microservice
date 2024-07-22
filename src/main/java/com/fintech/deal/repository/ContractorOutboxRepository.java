package com.fintech.deal.repository;

import com.fintech.deal.model.ContractorOutboxMessage;
import com.fintech.deal.model.Deal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for {@link ContractorOutboxMessage} entities.
 * @author Matushkin Anton
 */
@Repository
public interface ContractorOutboxRepository extends JpaRepository<ContractorOutboxMessage, Long> {

    List<ContractorOutboxMessage> findBySentFalse();

    @Query("SELECT d FROM Deal d " +
            "JOIN DealContractor dc ON d.id = dc.deal.id " +
            "JOIN ContractorOutboxMessage com ON dc.contractorId = com.contractorId " +
            "WHERE com.contractorId = :contractorId")
    Deal findDealsByContractorId(String contractorId);

}
