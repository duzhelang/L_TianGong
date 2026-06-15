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
@Table(name = "iot_device")
public class IotDevice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String deviceCode;

    @Column(nullable = false, length = 128)
    private String deviceName;

    @Column(nullable = false, length = 32)
    private String deviceType;

    @Column(length = 64)
    private String productKey;

    private Long projectId;

    @Column(length = 128)
    private String location;

    private Double longitude;

    private Double latitude;

    @Column(nullable = false, length = 16)
    private String status = "OFFLINE";

    private LocalDateTime lastOnlineAt;

    @Column(columnDefinition = "TEXT")
    private String configJson;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
