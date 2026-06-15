package com.ecocarbon.mrv.controller;

import com.ecocarbon.mrv.common.ApiResponse;
import com.ecocarbon.mrv.entity.SysRole;
import com.ecocarbon.mrv.entity.User;
import com.ecocarbon.mrv.repository.UserRepository;
import com.ecocarbon.mrv.service.SysUserRoleService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class SysUserController {
    private final UserRepository userRepository;
    private final SysUserRoleService userRoleService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Page<User>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok(userRepository.findAll(PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<User> getById(@PathVariable Long id) {
        return ApiResponse.ok(userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在")));
    }

    @GetMapping("/{id}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<SysRole>> getUserRoles(@PathVariable Long id) {
        return ApiResponse.ok(userRoleService.getUserRoles(id));
    }

    @PostMapping("/{id}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> assignRoles(@PathVariable Long id, @RequestBody List<Long> roleIds) {
        userRoleService.setUserRoles(id, roleIds);
        return ApiResponse.ok("角色分配成功", null);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ApiResponse.ok("用户删除成功", null);
    }
}
