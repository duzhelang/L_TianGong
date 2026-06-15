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
@Table(name = "remote_sensing_data")
public class RemoteSensingData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private CarbonProject project;
    
    @Column(name = "data_type", nullable = false)
    private String dataType;
    
    @Column(name = "satellite_source")
    private String satelliteSource;
    
    @Column(name = "acquisition_date", nullable = false)
    private LocalDate acquisitionDate;
    
    private String resolution;
    
    @Column(name = "cloud_cover")
    private Double cloudCover;
    
    @Column(name = "ndvi_value")
    private Double ndviValue;
    
    @Column(name = "lai_value")
    private Double laiValue;
    
    @Column(name = "biomass_value")
    private Double biomassValue;
    
    @Column(name = "file_path")
    private String filePath;
    
    private String status;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}