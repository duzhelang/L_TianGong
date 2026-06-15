package com.ecocarbon.mrv.repository;

import com.ecocarbon.mrv.entity.SysRolePermission;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SysRolePermissionRepository extends JpaRepository<SysRolePermission, Long> {
    List<SysRolePermission> findByRoleId(Long roleId);

    boolean existsByRoleIdAndPermissionId(Long roleId, Long permissionId);
}
