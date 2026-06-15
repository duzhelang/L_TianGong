package com.ecocarbon.mrv.controller;

import com.ecocarbon.mrv.client.*;
import com.ecocarbon.mrv.common.ApiResponse;
import com.ecocarbon.mrv.service.DataSyncService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/external")
@RequiredArgsConstructor
public class ExternalDataController {

    private final WeatherApiClient weatherApiClient;
    private final EmissionFactorApiClient emissionFactorApiClient;
    private final RemoteSensingApiClient remoteSensingApiClient;
    private final SoilDataApiClient soilDataApiClient;
    private final DataSyncService dataSyncService;

    @GetMapping("/weather/current")
    public ApiResponse<JsonNode> getCurrentWeather(
            @RequestParam double longitude,
            @RequestParam double latitude) {
        JsonNode data = weatherApiClient.getCurrentWeather(longitude, latitude);
        return ApiResponse.ok(data);
    }

    @GetMapping("/weather/forecast")
    public ApiResponse<JsonNode> getWeatherForecast(
            @RequestParam double longitude,
            @RequestParam double latitude,
            @RequestParam(defaultValue = "3") int days) {
        JsonNode data = weatherApiClient.getWeatherForecast(longitude, latitude, days);
        return ApiResponse.ok(data);
    }

    @GetMapping("/weather/solar")
    public ApiResponse<JsonNode> getSolarRadiation(
            @RequestParam double longitude,
            @RequestParam double latitude) {
        JsonNode data = weatherApiClient.getSolarRadiation(longitude, latitude);
        return ApiResponse.ok(data);
    }

    @GetMapping("/emission-factors/search")
    public ApiResponse<JsonNode> searchEmissionFactors(
            @RequestParam String keyword,
            @RequestParam(required = false) String category) {
        JsonNode data = emissionFactorApiClient.searchEmissionFactors(keyword, category);
        return ApiResponse.ok(data);
    }

    @GetMapping("/emission-factors/electricity")
    public ApiResponse<JsonNode> getElectricityEmissionFactor(
            @RequestParam String region,
            @RequestParam(defaultValue = "2024") int year) {
        JsonNode data = emissionFactorApiClient.getElectricityEmissionFactor(region, year);
        return ApiResponse.ok(data);
    }

    @GetMapping("/emission-factors/fuel")
    public ApiResponse<JsonNode> getFuelEmissionFactor(
            @RequestParam String fuelType) {
        JsonNode data = emissionFactorApiClient.getFuelEmissionFactor(fuelType);
        return ApiResponse.ok(data);
    }

    @GetMapping("/remote-sensing/ndvi")
    public ApiResponse<JsonNode> getNDVI(
            @RequestParam double longitude,
            @RequestParam double latitude,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        JsonNode data = remoteSensingApiClient.getNDVIData(longitude, latitude, startDate, endDate);
        return ApiResponse.ok(data);
    }

    @GetMapping("/remote-sensing/npp")
    public ApiResponse<JsonNode> getNPP(
            @RequestParam double longitude,
            @RequestParam double latitude,
            @RequestParam(defaultValue = "2024") int year) {
        JsonNode data = remoteSensingApiClient.getNPPData(longitude, latitude, year);
        return ApiResponse.ok(data);
    }

    @GetMapping("/remote-sensing/lai")
    public ApiResponse<JsonNode> getLAI(
            @RequestParam double longitude,
            @RequestParam double latitude,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        JsonNode data = remoteSensingApiClient.getLAIData(longitude, latitude, startDate, endDate);
        return ApiResponse.ok(data);
    }

    @GetMapping("/remote-sensing/land-cover")
    public ApiResponse<JsonNode> getLandCover(
            @RequestParam double longitude,
            @RequestParam double latitude,
            @RequestParam(defaultValue = "2024") int year) {
        JsonNode data = remoteSensingApiClient.getLandCoverData(longitude, latitude, year);
        return ApiResponse.ok(data);
    }

    @GetMapping("/soil/organic-carbon")
    public ApiResponse<JsonNode> getSoilOrganicCarbon(
            @RequestParam double longitude,
            @RequestParam double latitude) {
        JsonNode data = soilDataApiClient.getSoilOrganicCarbon(longitude, latitude);
        return ApiResponse.ok(data);
    }

    @GetMapping("/soil/type")
    public ApiResponse<JsonNode> getSoilType(
            @RequestParam double longitude,
            @RequestParam double latitude) {
        JsonNode data = soilDataApiClient.getSoilType(longitude, latitude);
        return ApiResponse.ok(data);
    }

    @GetMapping("/soil/texture")
    public ApiResponse<JsonNode> getSoilTexture(
            @RequestParam double longitude,
            @RequestParam double latitude) {
        JsonNode data = soilDataApiClient.getSoilTexture(longitude, latitude);
        return ApiResponse.ok(data);
    }

    @GetMapping("/soil/ph")
    public ApiResponse<JsonNode> getSoilPH(
            @RequestParam double longitude,
            @RequestParam double latitude) {
        JsonNode data = soilDataApiClient.getSoilPH(longitude, latitude);
        return ApiResponse.ok(data);
    }

    @PostMapping("/sync/all")
    public ApiResponse<String> syncAllData(
            @RequestParam double longitude,
            @RequestParam double latitude,
            @RequestParam Long projectId) {
        dataSyncService.syncAllData(longitude, latitude, projectId);
        return ApiResponse.ok("数据同步已启动");
    }

    @PostMapping("/sync/weather")
    public ApiResponse<String> syncWeatherData(
            @RequestParam double longitude,
            @RequestParam double latitude,
            @RequestParam Long projectId) {
        dataSyncService.syncWeatherData(longitude, latitude, projectId);
        return ApiResponse.ok("气象数据同步已启动");
    }

    @PostMapping("/sync/emission-factors")
    public ApiResponse<String> syncEmissionFactors(
            @RequestParam String keyword,
            @RequestParam(required = false) String category) {
        dataSyncService.syncEmissionFactors(keyword, category);
        return ApiResponse.ok("排放因子同步已启动");
    }

    @PostMapping("/sync/remote-sensing")
    public ApiResponse<String> syncRemoteSensingData(
            @RequestParam double longitude,
            @RequestParam double latitude,
            @RequestParam Long projectId) {
        dataSyncService.syncRemoteSensingData(longitude, latitude, projectId);
        return ApiResponse.ok("遥感数据同步已启动");
    }

    @PostMapping("/sync/soil")
    public ApiResponse<String> syncSoilData(
            @RequestParam double longitude,
            @RequestParam double latitude,
            @RequestParam Long projectId) {
        dataSyncService.syncSoilData(longitude, latitude, projectId);
        return ApiResponse.ok("土壤数据同步已启动");
    }
}