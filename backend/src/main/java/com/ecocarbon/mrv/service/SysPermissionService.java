package com.ecocarbon.mrv.service;

import com.ecocarbon.mrv.entity.SysPermission;
import com.ecocarbon.mrv.repository.SysPermissionRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SysPermissionService {
    private final SysPermissionRepository permissionRepository;

    public Page<SysPermission> list(Pageable pageable) {
        return permissionRepository.findAll(pageable);
    }

    public List<SysPermission> listAll() {
        return permissionRepository.findAll();
    }

    public SysPermission getById(Long id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("权限不存在"));
    }

    @Transactional
    public SysPermission create(SysPermission permission) {
        if (permissionRepository.existsByPermissionCode(permission.getPermissionCode())) {
            throw new IllegalArgumentException("权限编码已存在: " + permission.getPermissionCode());
        }
        permission.setCreatedAt(LocalDateTime.now());
        return permissionRepository.save(permission);
    }

    @Transactional
    public SysPermission update(Long id, SysPermission permission) {
        SysPermission existing = getById(id);
        existing.setPermissionName(permission.getPermissionName());
        existing.setResourceType(permission.getResourceType());
        existing.setResourcePath(permission.getResourcePath());
        existing.setDescription(permission.getDescription());
        return permissionRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        permissionRepository.deleteById(id);
    }
}
