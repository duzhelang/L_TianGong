package com.ecocarbon.mrv.service;

import com.ecocarbon.mrv.entity.SysRole;
import com.ecocarbon.mrv.entity.SysRolePermission;
import com.ecocarbon.mrv.entity.SysUserRole;
import com.ecocarbon.mrv.repository.SysRolePermissionRepository;
import com.ecocarbon.mrv.repository.SysRoleRepository;
import com.ecocarbon.mrv.repository.SysUserRoleRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SysUserRoleService {
    private final SysUserRoleRepository userRoleRepository;
    private final SysRoleRepository roleRepository;
    private final SysRolePermissionRepository rolePermissionRepository;

    public List<SysRole> getUserRoles(Long userId) {
        return userRoleRepository.findByUserId(userId).stream()
                .map(ur -> roleRepository.findById(ur.getRoleId()).orElse(null))
                .filter(role -> role != null)
                .toList();
    }

    public List<String> getUserRoleCodes(Long userId) {
        return getUserRoles(userId).stream()
                .map(SysRole::getRoleCode)
                .toList();
    }

    public List<String> getUserPermissionCodes(Long userId) {
        List<Long> roleIds = userRoleRepository.findByUserId(userId).stream()
                .map(SysUserRole::getRoleId)
                .toList();
        return rolePermissionRepository.findByRoleId(roleIds.get(0)).stream()
                .map(SysRolePermission::getPermissionId)
                .distinct()
                .toList()
                .stream()
                .map(String::valueOf)
                .toList();
    }

    @Transactional
    public void assignRole(Long userId, Long roleId) {
        if (userRoleRepository.existsByUserIdAndRoleId(userId, roleId)) {
            return;
        }
        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(roleId);
        userRole.setCreatedAt(LocalDateTime.now());
        userRoleRepository.save(userRole);
    }

    @Transactional
    public void removeRole(Long userId, Long roleId) {
        userRoleRepository.findByUserId(userId).stream()
                .filter(ur -> ur.getRoleId().equals(roleId))
                .findFirst()
                .ifPresent(ur -> userRoleRepository.deleteById(ur.getId()));
    }

    @Transactional
    public void setUserRoles(Long userId, List<Long> roleIds) {
        List<SysUserRole> existing = userRoleRepository.findByUserId(userId);
        userRoleRepository.deleteAll(existing);
        roleIds.forEach(roleId -> assignRole(userId, roleId));
    }
}
