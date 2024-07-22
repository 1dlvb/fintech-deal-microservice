package com.fintech.deal.repository;

import com.fintech.deal.model.ContractorRole;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for {@link ContractorRole} entities.
 * @author Matushkin Anton
 */
public interface RoleRepository extends JpaRepository<ContractorRole, String> {


}
