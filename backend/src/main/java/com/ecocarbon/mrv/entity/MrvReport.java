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
@Table(name = "mrv_report")
public class MrvReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long projectId;

    @Column(nullable = false, length = 128)
    private String reportName;

    @Column(nullable = false, length = 32)
    private String reportType;

    private Long templateId;

    @Column(length = 256)
    private String filePath;

    private Long fileSize;

    @Column(nullable = false, length = 16)
    private String status;

    @Column(nullable = false)
    private Integer version;

    private Long createdBy;

    private Long reviewedBy;

    private LocalDateTime reviewedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
