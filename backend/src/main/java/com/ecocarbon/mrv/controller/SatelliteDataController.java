package com.ecocarbon.mrv.controller;

import com.ecocarbon.mrv.common.ApiResponse;
import com.ecocarbon.mrv.service.SatelliteDataService;
import java.time.LocalDate;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/satellite")
@RequiredArgsConstructor
public class SatelliteDataController {
    private final SatelliteDataService satelliteDataService;

    @GetMapping("/oco2")
    public ApiResponse<Map<String, Object>> getOCO2Data(
            @RequestParam double longitude,
            @RequestParam double latitude,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ApiResponse.ok(satelliteDataService.getOCO2Data(longitude, latitude, startDate.toString(), endDate.toString()));
    }

    @GetMapping("/gosat")
    public ApiResponse<Map<String, Object>> getGOSATData(
            @RequestParam double longitude,
            @RequestParam double latitude,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ApiResponse.ok(satelliteDataService.getGOSATData(longitude, latitude, startDate.toString(), endDate.toString()));
    }

    @GetMapping("/gpp")
    public ApiResponse<Map<String, Object>> getGPPData(
            @RequestParam double longitude,
            @RequestParam double latitude,
            @RequestParam int year) {
        return ApiResponse.ok(satelliteDataService.getGPPData(longitude, latitude, year));
    }
}
