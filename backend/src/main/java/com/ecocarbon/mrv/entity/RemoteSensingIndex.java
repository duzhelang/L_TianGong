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
@Table(name = "remote_sensing_index")
public class RemoteSensingIndex {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "area_id", nullable = false)
    private String areaId;

    @Column(name = "index_type", nullable = false)
    private String indexType;

    @Column(nullable = false)
    private Double value;

    private String unit;

    @Column(name = "capture_date", nullable = false)
    private LocalDate captureDate;

    private String resolution;

    private String source;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
