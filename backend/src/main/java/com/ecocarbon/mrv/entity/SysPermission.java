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
@Table(name = "sys_permission")
public class SysPermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64)
    private String permissionName;

    @Column(nullable = false, unique = true, length = 64)
    private String permissionCode;

    @Column(length = 32)
    private String resourceType;

    @Column(length = 128)
    private String resourcePath;

    @Column(length = 128)
    private String description;

    private LocalDateTime createdAt;
}
