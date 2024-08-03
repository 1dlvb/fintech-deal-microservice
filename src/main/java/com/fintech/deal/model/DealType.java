package com.fintech.deal.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class representing a deal type.
 * This class maps to the "deal_type" table in the database.
 * @author Matushkin Anton
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "deal_type")
public class DealType {

    @Id
    private String id;

    private String name;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

}
