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
@Table(name = "iot_alarm_record")
public class IotAlarmRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long ruleId;

    @Column(nullable = false, length = 64)
    private String deviceCode;

    @Column(nullable = false, length = 32)
    private String metric;

    private Double actualValue;

    private Double thresholdValue;

    @Column(nullable = false, length = 16)
    private String severity;

    @Column(nullable = false, length = 16)
    private String status = "ACTIVE";

    private Long acknowledgedBy;

    private LocalDateTime acknowledgedAt;

    private LocalDateTime resolvedAt;

    private LocalDateTime createdAt;
}
