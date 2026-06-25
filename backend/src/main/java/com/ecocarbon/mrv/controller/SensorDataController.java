package com.ecocarbon.mrv.controller;

import com.ecocarbon.mrv.common.ApiResponse;
import com.ecocarbon.mrv.entity.SensorData;
import com.ecocarbon.mrv.service.DemoSimulatorService;
import com.ecocarbon.mrv.service.SensorDataService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/sensor")
@RequiredArgsConstructor
public class SensorDataController {
    private final SensorDataService sensorDataService;
    private final DemoSimulatorService demoSimulatorService;

    @PostMapping("/data")
    public ApiResponse<?> receiveData(@RequestBody Object payload) {
        if (payload instanceof List<?> list) {
            List<SensorData> dataList = list.stream()
                    .map(this::convertToSensorData)
                    .toList();
            return ApiResponse.ok("批量数据接收成功", sensorDataService.saveAll(dataList).size());
        }
        SensorData data = convertToSensorData(payload);
        return ApiResponse.ok("数据接收成功", sensorDataService.save(data));
    }

    @GetMapping("/latest")
    public ApiResponse<SensorData> getLatest(
            @RequestParam String deviceCode,
            @RequestParam String metric) {
        return ApiResponse.ok(sensorDataService.getLatest(deviceCode, metric));
    }

    @GetMapping("/history")
    public ApiResponse<Page<SensorData>> getHistory(
            @RequestParam String deviceCode,
            @RequestParam(required = false) String metric,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return ApiResponse.ok(sensorDataService.getByTimeRange(
                deviceCode, metric, startTime, endTime, PageRequest.of(page, size)));
    }

    @GetMapping("/statistics")
    public ApiResponse<Map<String, Object>> getStatistics(
            @RequestParam String deviceCode,
            @RequestParam String metric,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return ApiResponse.ok(sensorDataService.getStatistics(deviceCode, metric, startTime, endTime));
    }

    @GetMapping("/device/{deviceCode}/latest")
    public ApiResponse<Map<String, Object>> getDeviceLatestAll(@PathVariable String deviceCode) {
        return ApiResponse.ok(sensorDataService.getDeviceLatestAll(deviceCode));
    }

    @PostMapping("/demo/start")
    public ApiResponse<?> startDemo() {
        demoSimulatorService.startDemo();
        return ApiResponse.ok("Demo模拟已启动", demoSimulatorService.getStatus());
    }

    @PostMapping("/demo/stop")
    public ApiResponse<?> stopDemo() {
        demoSimulatorService.stopDemo();
        return ApiResponse.ok("Demo模拟已停止", demoSimulatorService.getStatus());
    }

    @GetMapping("/demo/status")
    public ApiResponse<Map<String, Object>> getDemoStatus() {
        return ApiResponse.ok(demoSimulatorService.getStatus());
    }

    @SuppressWarnings("unchecked")
    private SensorData convertToSensorData(Object obj) {
        if (obj instanceof Map<?, ?> map) {
            Map<String, Object> m = (Map<String, Object>) map;
            SensorData data = new SensorData();
            data.setDeviceCode((String) m.get("deviceCode"));
            data.setDeviceType((String) m.get("deviceType"));
            data.setMetric((String) m.get("metric"));
            data.setValue(toDouble(m.get("value")));
            data.setUnit((String) m.get("unit"));
            data.setQuality((String) m.getOrDefault("quality", "NORMAL"));
            data.setTimestamp(LocalDateTime.now());
            data.setCreatedAt(LocalDateTime.now());
            return data;
        }
        throw new IllegalArgumentException("无效的传感器数据格式");
    }

    private Double toDouble(Object obj) {
        if (obj instanceof Number n) return n.doubleValue();
        if (obj instanceof String s) return Double.parseDouble(s);
        throw new IllegalArgumentException("数值格式错误");
    }
}
