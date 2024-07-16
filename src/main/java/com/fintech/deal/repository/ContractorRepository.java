package com.fintech.deal.repository;

import com.fintech.deal.model.DealContractor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ContractorRepository extends JpaRepository<DealContractor, UUID> {

}
