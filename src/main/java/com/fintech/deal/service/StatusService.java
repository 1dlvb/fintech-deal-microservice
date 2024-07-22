package com.fintech.deal.service;

import com.fintech.deal.model.DealStatus;

/**
 * Service interface for managing deal statuses.
 * @author Matushkin Anton
 */
public interface StatusService {

    /**
     * Retrieves a deal status by its unique identifier.
     * @param id The unique identifier of the deal status to be retrieved.
     * @return The {@link DealStatus} object corresponding to the given ID.
     */
    DealStatus getStatusById(String id);

}
