package com.fintech.deal.service;

import com.fintech.deal.dto.ContractorDTO;
import com.fintech.deal.dto.RoleDTO;
import com.fintech.deal.exception.NotActiveException;

import java.util.UUID;

/**
 * Service interface for managing contractors and their roles.
 * This interface defines methods for crud operations.
 * @author Matushkin Anton
 */
public interface ContractorService {

    ContractorDTO saveContractor(ContractorDTO contractorDTO);

    void deleteContractor(UUID id) throws NotActiveException;

    ContractorDTO addRole(UUID id, RoleDTO roleDTO) throws NotActiveException;

    /**
     * Deletes a role associated with a contractor by the role's unique identifier.
     * @param id The unique identifier of the {@link com.fintech.deal.model.DealContractorRole} to be deleted.
     */
    void deleteRoleByDealContractorRoleId(UUID id);

}
