package com.ecocarbon.mrv.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "carbon_asset")
public class CarbonAsset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String assetCode;

    @Column(nullable = false, length = 128)
    private String assetName;

    @Column(nullable = false, length = 32)
    private String assetType;

    private Long projectId;

    private Long methodologyId;

    @Column(nullable = false)
    private Double quantity;

    @Column(nullable = false, length = 16)
    private String unit = "tCO2e";

    private Integer vintageYear;

    private LocalDate issuanceDate;

    private LocalDate expiryDate;

    @Column(length = 64)
    private String registryId;

    @Column(length = 128)
    private String serialNumber;

    @Column(nullable = false, length = 16)
    private String status = "ACTIVE";

    private Long ownerId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
