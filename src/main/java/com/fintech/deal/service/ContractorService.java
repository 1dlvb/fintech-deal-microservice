package com.fintech.deal.service;

import com.fintech.deal.dto.ContractorDTO;
import com.fintech.deal.dto.RoleDTO;
import com.fintech.deal.exception.NotActiveException;

import java.util.UUID;

public interface ContractorService {

    ContractorDTO saveContractor(ContractorDTO contractorDTO);

    void deleteContractor(UUID id) throws NotActiveException;

    ContractorDTO addRole(UUID id, RoleDTO roleDTO) throws NotActiveException;

}
