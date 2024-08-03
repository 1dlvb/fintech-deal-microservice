package com.fintech.deal.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

import java.util.UUID;

/**
 * Entity class representing a deal contactor role.
 * This class maps to the "deal_contractor_role" table in the database.
 * @author Matushkin Anton
 */
@Data
@Entity
@Table(name = "deal_contractor_role", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"deal_contractor_id", "contractor_role_id"})})
public class DealContractorRole {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "deal_contractor_id")
    private DealContractor dealContractor;

    @ManyToOne
    @JoinColumn(name = "contractor_role_id")
    private ContractorRole contractorRole;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

}
