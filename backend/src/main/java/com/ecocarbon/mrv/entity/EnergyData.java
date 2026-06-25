package com.ecocarbon.mrv.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "energy_data", indexes = {
    @Index(name = "idx_energy_project_date", columnList = "project_id,record_date"),
    @Index(name = "idx_energy_type_date", columnList = "energy_type,record_date")
})
public class EnergyData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private CarbonProject project;

    @Column(nullable = false, length = 32)
    private String energyType;

    @Column(nullable = false, length = 32)
    private String energyCategory;

    @Column(nullable = false)
    private Double consumption;

    @Column(nullable = false, length = 16)
    private String unit;

    @Column(nullable = false)
    private Double emissionFactor;

    @Column(length = 16)
    private String emissionUnit;

    @Column(nullable = false)
    private Double carbonEmission;

    @Column(nullable = false)
    private LocalDate recordDate;

    @Column(length = 64)
    private String source;

    @Column(length = 16)
    private String quality = "NORMAL";

    @Column(length = 256)
    private String remark;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
