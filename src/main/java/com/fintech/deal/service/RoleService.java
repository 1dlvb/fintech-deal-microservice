package com.fintech.deal.service;

import com.fintech.deal.model.ContractorRole;

/**
 * Service interface for managing contractor roles.
 * @author Matushkin Anton
 */
public interface RoleService {

    /**
     * Retrieves a contractor role by its unique identifier.
     * @param id The unique identifier of the contractor role to be retrieved.
     * @return The {@link ContractorRole} object corresponding to the given ID.
     */
    ContractorRole findRoleById(String id);

}
