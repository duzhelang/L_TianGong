package com.ecocarbon.mrv.controller;

import com.ecocarbon.mrv.common.ApiResponse;
import com.ecocarbon.mrv.service.LandUseChangeService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/land-use")
@RequiredArgsConstructor
public class LandUseChangeController {
    private final LandUseChangeService landUseChangeService;

    @GetMapping("/changes")
    public ApiResponse<Map<String, Object>> detectChanges(
            @RequestParam String areaId,
            @RequestParam int year1,
            @RequestParam int year2) {
        return ApiResponse.ok(landUseChangeService.detectChanges(areaId, year1, year2));
    }

    @GetMapping("/time-series")
    public ApiResponse<List<Map<String, Object>>> getTimeSeries(@RequestParam String areaId) {
        return ApiResponse.ok(landUseChangeService.getTimeSeries(areaId));
    }

    @GetMapping("/transfer-matrix")
    public ApiResponse<Map<String, Object>> getTransferMatrix(
            @RequestParam String areaId,
            @RequestParam int year1,
            @RequestParam int year2) {
        return ApiResponse.ok(landUseChangeService.getTransferMatrix(areaId, year1, year2));
    }
}
