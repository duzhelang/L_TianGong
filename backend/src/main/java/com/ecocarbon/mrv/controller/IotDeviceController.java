package com.ecocarbon.mrv.controller;

import com.ecocarbon.mrv.common.ApiResponse;
import com.ecocarbon.mrv.entity.IotDevice;
import com.ecocarbon.mrv.service.IotDeviceService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/iot/devices")
@RequiredArgsConstructor
public class IotDeviceController {
    private final IotDeviceService deviceService;

    @GetMapping
    public ApiResponse<Page<IotDevice>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok(deviceService.list(PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    public ApiResponse<IotDevice> getById(@PathVariable Long id) {
        return ApiResponse.ok(deviceService.getById(id));
    }

    @GetMapping("/code/{deviceCode}")
    public ApiResponse<IotDevice> getByCode(@PathVariable String deviceCode) {
        return ApiResponse.ok(deviceService.getByCode(deviceCode));
    }

    @GetMapping("/type/{deviceType}")
    public ApiResponse<List<IotDevice>> getByType(@PathVariable String deviceType) {
        return ApiResponse.ok(deviceService.getByType(deviceType));
    }

    @GetMapping("/project/{projectId}")
    public ApiResponse<List<IotDevice>> getByProject(@PathVariable Long projectId) {
        return ApiResponse.ok(deviceService.getByProject(projectId));
    }

    @GetMapping("/online")
    public ApiResponse<List<IotDevice>> getOnlineDevices() {
        return ApiResponse.ok(deviceService.getOnlineDevices());
    }

    @PostMapping
    public ApiResponse<IotDevice> create(@RequestBody IotDevice device) {
        return ApiResponse.ok("设备注册成功", deviceService.create(device));
    }

    @PutMapping("/{id}")
    public ApiResponse<IotDevice> update(@PathVariable Long id, @RequestBody IotDevice device) {
        return ApiResponse.ok("设备更新成功", deviceService.update(id, device));
    }

    @PutMapping("/{code}/status")
    public ApiResponse<Void> updateStatus(@PathVariable String code, @RequestParam String status) {
        deviceService.updateStatus(code, status);
        return ApiResponse.ok("状态更新成功", null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        deviceService.delete(id);
        return ApiResponse.ok("设备删除成功", null);
    }
}
