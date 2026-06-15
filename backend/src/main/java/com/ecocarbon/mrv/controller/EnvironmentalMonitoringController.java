package com.ecocarbon.mrv.controller;

import com.ecocarbon.mrv.common.ApiResponse;
import com.ecocarbon.mrv.dto.EnvironmentCorrelationResponse;
import com.ecocarbon.mrv.service.CarbonEnvironmentCorrelation;
import com.ecocarbon.mrv.service.EnvironmentalMonitoringService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/environment")
@RequiredArgsConstructor
public class EnvironmentalMonitoringController {
    private final EnvironmentalMonitoringService environmentalMonitoringService;
    private final CarbonEnvironmentCorrelation carbonEnvironmentCorrelation;

    @GetMapping("/parameters")
    public ApiResponse<Map<String, Double>> getEnvironmentParameters(@RequestParam String areaId) {
        return ApiResponse.ok("环境参数获取成功", environmentalMonitoringService.getEnvironmentParameters(areaId));
    }

    @GetMapping("/correlation")
    public ApiResponse<EnvironmentCorrelationResponse> getCorrelationAnalysis(@RequestParam String areaId) {
        Map<String, Double> environmentData = environmentalMonitoringService.getEnvironmentParameters(areaId);
        return ApiResponse.ok("碳过程关联分析完成", carbonEnvironmentCorrelation.analyzeCorrelation(areaId, environmentData));
    }

    @GetMapping("/alerts")
    public ApiResponse<List<Map<String, Object>>> getEnvironmentAlerts(@RequestParam String areaId) {
        return ApiResponse.ok("环境预警获取成功", environmentalMonitoringService.getEnvironmentAlerts(areaId));
    }
}
