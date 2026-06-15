package com.ecocarbon.mrv.service;

import com.ecocarbon.mrv.entity.SysRole;
import com.ecocarbon.mrv.repository.SysRoleRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SysRoleService {
    private final SysRoleRepository roleRepository;

    public Page<SysRole> list(Pageable pageable) {
        return roleRepository.findAll(pageable);
    }

    public SysRole getById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("角色不存在"));
    }

    public SysRole getByCode(String roleCode) {
        return roleRepository.findByRoleCode(roleCode)
                .orElseThrow(() -> new IllegalArgumentException("角色不存在: " + roleCode));
    }

    @Transactional
    public SysRole create(SysRole role) {
        if (roleRepository.existsByRoleCode(role.getRoleCode())) {
            throw new IllegalArgumentException("角色编码已存在: " + role.getRoleCode());
        }
        role.setCreatedAt(LocalDateTime.now());
        role.setUpdatedAt(LocalDateTime.now());
        return roleRepository.save(role);
    }

    @Transactional
    public SysRole update(Long id, SysRole role) {
        SysRole existing = getById(id);
        existing.setRoleName(role.getRoleName());
        existing.setDescription(role.getDescription());
        existing.setStatus(role.getStatus());
        existing.setUpdatedAt(LocalDateTime.now());
        return roleRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        roleRepository.deleteById(id);
    }
}
