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
@Table(name = "methodology_config")
public class MethodologyConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "methodology_code", unique = true, nullable = false)
    private String methodologyCode;
    
    @Column(name = "methodology_name", nullable = false)
    private String methodologyName;
    
    private String version;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "applicable_scenes")
    private String applicableScenes;
    
    @Column(name = "calculation_formula", columnDefinition = "TEXT")
    private String calculationFormula;
    
    @Column(name = "parameters_config", columnDefinition = "JSON")
    private String parametersConfig;
    
    @Column(name = "reference_standard")
    private String referenceStandard;
    
    private String status;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}