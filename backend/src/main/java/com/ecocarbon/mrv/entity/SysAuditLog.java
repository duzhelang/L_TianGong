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
@Table(name = "sys_audit_log")
public class SysAuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Column(length = 64)
    private String username;

    @Column(length = 32)
    private String operation;

    @Column(length = 128)
    private String method;

    @Column(columnDefinition = "TEXT")
    private String params;

    @Column(length = 64)
    private String ip;

    private Long duration;

    private Integer status;

    @Column(columnDefinition = "TEXT")
    private String errorMsg;

    private LocalDateTime createdAt;
}
