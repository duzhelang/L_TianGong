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
@Table(name = "carbon_project")
public class CarbonProject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 128)
    private String name;

    @Column(length = 64)
    private String sceneType;

    @Column(length = 128)
    private String areaName;

    private Double areaHa;

    @Column(length = 512)
    private String description;

    @Column(length = 32)
    private String status;

    @Column(length = 64)
    private String methodology;

    private Integer baselineYear;

    private Double targetReduction;

    private LocalDate startDate;

    private LocalDate endDate;

    @Column(length = 64)
    private String contactPerson;

    @Column(length = 32)
    private String contactPhone;

    private Long createdBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
