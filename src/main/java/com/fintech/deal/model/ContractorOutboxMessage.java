package com.fintech.deal.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity class representing a contractor outbox messages.
 * This class maps to the "contractor_outbox_message" table in the database.
 * It includes basic information about a contractor entity and audit-related fields.
 * @author Matushkin Anton
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "contractor_outbox_message")
public class ContractorOutboxMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false, name = "contractor_id")
    private String contractorId;

    @Column(nullable = false, name = "active_main_borrower")
    private boolean activeMainBorrower;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MessageStatus status;

    private boolean sent = false;

    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

}
