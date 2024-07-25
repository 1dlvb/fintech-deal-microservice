package com.fintech.deal.repository;

import com.fintech.deal.model.DealType;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for {@link DealType} entities.
 * @author Matushkin Anton
 */
public interface TypeRepository extends JpaRepository<DealType, String> {

    DealType getDealTypeById(String id);

}
