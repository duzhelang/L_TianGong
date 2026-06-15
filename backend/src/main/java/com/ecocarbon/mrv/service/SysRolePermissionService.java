package com.ecocarbon.mrv.service;

import com.ecocarbon.mrv.entity.SysRolePermission;
import com.ecocarbon.mrv.repository.SysRolePermissionRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SysRolePermissionService {
    private final SysRolePermissionRepository rolePermissionRepository;

    public List<SysRolePermission> getRolePermissions(Long roleId) {
        return rolePermissionRepository.findByRoleId(roleId);
    }

    @Transactional
    public void assignPermission(Long roleId, Long permissionId) {
        if (rolePermissionRepository.existsByRoleIdAndPermissionId(roleId, permissionId)) {
            return;
        }
        SysRolePermission rp = new SysRolePermission();
        rp.setRoleId(roleId);
        rp.setPermissionId(permissionId);
        rp.setCreatedAt(LocalDateTime.now());
        rolePermissionRepository.save(rp);
    }

    @Transactional
    public void removePermission(Long roleId, Long permissionId) {
        rolePermissionRepository.findByRoleId(roleId).stream()
                .filter(rp -> rp.getPermissionId().equals(permissionId))
                .findFirst()
                .ifPresent(rp -> rolePermissionRepository.deleteById(rp.getId()));
    }

    @Transactional
    public void setRolePermissions(Long roleId, List<Long> permissionIds) {
        List<SysRolePermission> existing = rolePermissionRepository.findByRoleId(roleId);
        rolePermissionRepository.deleteAll(existing);
        permissionIds.forEach(permissionId -> assignPermission(roleId, permissionId));
    }
}
