package com.ecocarbon.mrv.repository;

import com.ecocarbon.mrv.entity.SysRole;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SysRoleRepository extends JpaRepository<SysRole, Long> {
    Optional<SysRole> findByRoleCode(String roleCode);

    boolean existsByRoleCode(String roleCode);
}
