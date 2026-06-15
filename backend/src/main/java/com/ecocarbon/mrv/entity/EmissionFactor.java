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
@Table(name = "emission_factor")
public class EmissionFactor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "factor_code", unique = true, nullable = false)
    private String factorCode;
    
    @Column(name = "factor_name", nullable = false)
    private String factorName;
    
    @Column(nullable = false)
    private String category;
    
    private String subcategory;
    
    @Column(name = "gas_type", nullable = false)
    private String gasType;
    
    @Column(name = "factor_value", nullable = false)
    private Double factorValue;
    
    @Column(nullable = false)
    private String unit;
    
    private String source;
    
    private String region;
    
    private Integer year;
    
    private String status;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}