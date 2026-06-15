package com.ecocarbon.mrv.repository;

import com.ecocarbon.mrv.entity.SysUserRole;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SysUserRoleRepository extends JpaRepository<SysUserRole, Long> {
    List<SysUserRole> findByUserId(Long userId);

    List<SysUserRole> findByRoleId(Long roleId);

    boolean existsByUserIdAndRoleId(Long userId, Long roleId);
}
