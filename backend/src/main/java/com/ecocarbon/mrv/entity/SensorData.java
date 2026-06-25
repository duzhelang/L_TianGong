package com.ecocarbon.mrv.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "sensor_data", indexes = {
    @Index(name = "idx_sensor_device_time", columnList = "deviceCode,timestamp"),
    @Index(name = "idx_sensor_metric_time", columnList = "metric,timestamp"),
    @Index(name = "idx_sensor_timestamp", columnList = "timestamp")
})
public class SensorData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64)
    private String deviceCode;

    @Column(length = 32)
    private String deviceType;

    @Column(nullable = false, length = 32)
    private String metric;

    @Column(nullable = false)
    private Double value;

    @Column(length = 16)
    private String unit;

    @Column(length = 16)
    private String quality = "NORMAL";

    @Column(nullable = false)
    private LocalDateTime timestamp;

    private LocalDateTime createdAt;
}
