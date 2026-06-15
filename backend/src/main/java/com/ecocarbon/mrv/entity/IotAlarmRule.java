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
@Table(name = "iot_alarm_rule")
public class IotAlarmRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 128)
    private String ruleName;

    @Column(length = 32)
    private String deviceType;

    @Column(length = 64)
    private String deviceCode;

    @Column(nullable = false, length = 32)
    private String metric;

    @Column(nullable = false, length = 32)
    private String condition;

    @Column(nullable = false)
    private Double thresholdValue;

    private Double thresholdValue2;

    @Column(nullable = false, length = 16)
    private String severity;

    @Column(nullable = false)
    private Integer enabled = 1;

    private LocalDateTime createdAt;
}
