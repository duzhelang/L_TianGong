package com.ecocarbon.mrv.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "carbon_transaction")
public class CarbonTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String transactionCode;

    @Column(nullable = false)
    private Long assetId;

    @Column(nullable = false, length = 16)
    private String orderType;

    @Column(nullable = false)
    private Double quantity;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Double totalAmount;

    @Column(nullable = false, length = 16)
    private String currency = "CNY";

    @Column(length = 32)
    private String exchange;

    private Long counterpartyId;

    @Column(nullable = false, length = 16)
    private String status = "PENDING";

    private LocalDateTime submittedAt;

    private LocalDateTime matchedAt;

    private LocalDateTime settledAt;

    private Long createdBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
