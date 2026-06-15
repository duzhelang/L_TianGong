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
@Table(name = "sys_role")
public class SysRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 32)
    private String roleName;

    @Column(nullable = false, unique = true, length = 32)
    private String roleCode;

    @Column(length = 128)
    private String description;

    @Column(nullable = false)
    private Integer status = 1;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
