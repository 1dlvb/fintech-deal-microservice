package com.fintech.deal.repository;

import com.fintech.deal.model.DealStatus;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for {@link DealStatus} entities.
 * @author Matushkin Anton
 */
public interface StatusRepository extends JpaRepository<DealStatus, String> {

}
