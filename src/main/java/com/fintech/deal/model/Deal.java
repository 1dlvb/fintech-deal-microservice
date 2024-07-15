package com.fintech.deal.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "deal")
public class Deal {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "description")
    private String description;

    @Column(name = "agreement_number")
    private String agreementNumber;

    @Column(name = "agreement_date")
    private LocalDate agreementDate;

    @Column(name = "agreement_start_dts")
    private LocalDateTime agreementStartDt;

    @Column(name = "availability_date")
    private LocalDate availabilityDate;

    @ManyToOne
    @JoinColumn(name = "type")
    private DealType type;

    @ManyToOne
    @JoinColumn(name = "status", nullable = false)
    private DealStatus status;

    @Column(name = "sum", precision = 100, scale = 2)
    private BigDecimal sum;

    @Column(name = "close_dt")
    private LocalDateTime closeDt;

    @CreatedDate
    @Column(name = "create_date", nullable = false)
    private LocalDate createDate;

    @LastModifiedDate
    @Column(name = "modify_date")
    private LocalDate modifyDate;

    @CreatedBy
    @Column(name = "create_user_id")
    private String createUserId;

    @LastModifiedBy
    @Column(name = "modify_user_id")
    private String modifyUserId;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

}
