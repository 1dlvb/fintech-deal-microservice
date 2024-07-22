package com.fintech.deal.service;

import com.fintech.deal.dto.ResponseDealDTO;
import com.fintech.deal.dto.ChangeStatusOfDealDTO;
import com.fintech.deal.dto.DealWithContractorsDTO;
import com.fintech.deal.dto.SaveOrUpdateDealDTO;
import com.fintech.deal.model.Deal;
import com.fintech.deal.model.DealContractor;
import com.fintech.deal.payload.SearchDealPayload;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing deals.
 * @author Matushkin Anton
 */
public interface DealService {

    /**
     * Saves a new deal or updates an existing one.
     * @param saveOrUpdateDealDTO The {@link SaveOrUpdateDealDTO} object containing deal details
     *                            to be saved or updated.
     * @return The saved or updated {@link ResponseDealDTO}.
     */
    ResponseDealDTO saveDeal(SaveOrUpdateDealDTO saveOrUpdateDealDTO);

    /**
     * Changes the status of an existing deal.
     * @param changeStatusOfDealDTO The {@link ChangeStatusOfDealDTO} object containing
     *                               deal ID and the new status.
     * @return The updated {@link ResponseDealDTO}.
     */
    ResponseDealDTO changeStatus(ChangeStatusOfDealDTO changeStatusOfDealDTO);

    /**
     * Retrieves a deal by its unique identifier.
     * @param id The unique identifier of the deal to be retrieved.
     * @return The {@link Deal} object corresponding to the given ID.
     */
    Deal getDealById(UUID id);


    /**
     * Retrieves a deal along with its contractors by its unique identifier.
     * @param id The unique identifier of the deal.
     * @return The {@link DealWithContractorsDTO} object containing deal and contractor details.
     */
    DealWithContractorsDTO getDealWithContractorsById(UUID id);

    /**
     * Retrieves a list of contractors associated with a deal.
     * @param dealID The unique identifier of the deal.
     * @return A {@link List} of {@link DealContractor} objects associated with the deal.
     */
    List<DealContractor> getListOfContractorsByDealId(UUID dealID);

    /**
     * Searches for deals based on the given search criteria.
     * @param payload The {@link SearchDealPayload} object containing search criteria.
     * @param pageable The {@link Pageable} object for pagination information.
     * @return A {@link Page} of {@link DealWithContractorsDTO} objects matching the search criteria.
     */
    Page<DealWithContractorsDTO> searchDeals(SearchDealPayload payload, Pageable pageable);

}
