package com.ecocarbon.mrv.controller;

import com.ecocarbon.mrv.common.ApiResponse;
import com.ecocarbon.mrv.entity.IotAlarmRecord;
import com.ecocarbon.mrv.entity.IotAlarmRule;
import com.ecocarbon.mrv.service.IotAlarmService;
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
@RequestMapping("/api/v1/iot/alarms")
@RequiredArgsConstructor
public class IotAlarmController {
    private final IotAlarmService alarmService;

    @GetMapping("/rules")
    public ApiResponse<List<IotAlarmRule>> listRules() {
        return ApiResponse.ok(alarmService.listRules());
    }

    @GetMapping("/rules/{id}")
    public ApiResponse<IotAlarmRule> getRuleById(@PathVariable Long id) {
        return ApiResponse.ok(alarmService.getRuleById(id));
    }

    @PostMapping("/rules")
    public ApiResponse<IotAlarmRule> createRule(@RequestBody IotAlarmRule rule) {
        return ApiResponse.ok("规则创建成功", alarmService.createRule(rule));
    }

    @PutMapping("/rules/{id}")
    public ApiResponse<IotAlarmRule> updateRule(@PathVariable Long id, @RequestBody IotAlarmRule rule) {
        return ApiResponse.ok("规则更新成功", alarmService.updateRule(id, rule));
    }

    @DeleteMapping("/rules/{id}")
    public ApiResponse<Void> deleteRule(@PathVariable Long id) {
        alarmService.deleteRule(id);
        return ApiResponse.ok("规则删除成功", null);
    }

    @GetMapping("/records")
    public ApiResponse<Page<IotAlarmRecord>> listRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok(alarmService.listRecords(PageRequest.of(page, size)));
    }

    @GetMapping("/records/active")
    public ApiResponse<Page<IotAlarmRecord>> getActiveAlarms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok(alarmService.getActiveAlarms(PageRequest.of(page, size)));
    }

    @GetMapping("/records/device/{deviceCode}")
    public ApiResponse<List<IotAlarmRecord>> getDeviceAlarms(@PathVariable String deviceCode) {
        return ApiResponse.ok(alarmService.getDeviceAlarms(deviceCode));
    }

    @PutMapping("/records/{id}/acknowledge")
    public ApiResponse<IotAlarmRecord> acknowledge(@PathVariable Long id, @RequestParam Long userId) {
        return ApiResponse.ok("告警已确认", alarmService.acknowledge(id, userId));
    }

    @PutMapping("/records/{id}/resolve")
    public ApiResponse<IotAlarmRecord> resolve(@PathVariable Long id) {
        return ApiResponse.ok("告警已解决", alarmService.resolve(id));
    }

    @GetMapping("/count/active")
    public ApiResponse<Long> getActiveAlarmCount() {
        return ApiResponse.ok(alarmService.getActiveAlarmCount());
    }
}
