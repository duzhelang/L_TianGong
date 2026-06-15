package com.ecocarbon.mrv.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "carbon_emission")
public class CarbonEmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private CarbonProject project;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_data_id")
    private ActivityData activityData;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emission_factor_id")
    private EmissionFactor emissionFactor;
    
    @Column(nullable = false)
    private String scope;
    
    @Column(name = "gas_type", nullable = false)
    private String gasType;
    
    @Column(name = "emission_value", nullable = false)
    private Double emissionValue;
    
    @Column(nullable = false)
    private String unit;
    
    @Column(name = "calculation_method")
    private String calculationMethod;
    
    @Column(name = "period_start")
    private LocalDate periodStart;
    
    @Column(name = "period_end")
    private LocalDate periodEnd;
    
    private String status;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}