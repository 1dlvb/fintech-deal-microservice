package com.fintech.deal.repository;

import com.fintech.deal.model.OutboxMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxRepository extends JpaRepository<OutboxMessage, Long> {

    List<OutboxMessage> findBySentFalse();

}
