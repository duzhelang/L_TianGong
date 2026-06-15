package com.ecocarbon.mrv.repository;

import com.ecocarbon.mrv.entity.SysPermission;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SysPermissionRepository extends JpaRepository<SysPermission, Long> {
    Optional<SysPermission> findByPermissionCode(String permissionCode);

    boolean existsByPermissionCode(String permissionCode);
}
