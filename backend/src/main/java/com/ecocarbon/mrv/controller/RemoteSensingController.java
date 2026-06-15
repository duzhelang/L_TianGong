package com.ecocarbon.mrv.controller;

import com.ecocarbon.mrv.common.ApiResponse;
import com.ecocarbon.mrv.dto.BiomassEstimationRequest;
import com.ecocarbon.mrv.dto.BiomassEstimationResponse;
import com.ecocarbon.mrv.dto.NPPInversionRequest;
import com.ecocarbon.mrv.dto.NPPInversionResponse;
import com.ecocarbon.mrv.entity.LandUseData;
import com.ecocarbon.mrv.entity.NPPData;
import com.ecocarbon.mrv.entity.RemoteSensingIndex;
import com.ecocarbon.mrv.service.BiomassEstimationService;
import com.ecocarbon.mrv.service.NPPInversionService;
import com.ecocarbon.mrv.service.RemoteSensingService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/remote-sensing")
@RequiredArgsConstructor
public class RemoteSensingController {
    private final RemoteSensingService remoteSensingService;
    private final NPPInversionService nppInversionService;
    private final BiomassEstimationService biomassEstimationService;

    @GetMapping("/ndvi")
    public ApiResponse<List<RemoteSensingIndex>> getNDVIData(
            @RequestParam String areaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ApiResponse.ok("NDVI数据获取成功", remoteSensingService.getNDVIData(areaId, startDate, endDate));
    }

    @GetMapping("/npp")
    public ApiResponse<List<NPPData>> getNPPTimeSeries(
            @RequestParam String areaId,
            @RequestParam Integer startYear,
            @RequestParam Integer endYear) {
        return ApiResponse.ok("NPP时序数据获取成功", remoteSensingService.getNPPTimeSeries(areaId, startYear, endYear));
    }

    @GetMapping("/land-use")
    public ApiResponse<List<LandUseData>> getLandUseData(
            @RequestParam String areaId,
            @RequestParam Integer year) {
        return ApiResponse.ok("土地利用数据获取成功", remoteSensingService.getLandUseData(areaId, year));
    }

    @PostMapping("/biomass")
    public ApiResponse<BiomassEstimationResponse> estimateBiomass(
            @RequestBody @Valid BiomassEstimationRequest request) {
        return ApiResponse.ok("生物量估算完成", biomassEstimationService.estimateBiomass(request));
    }

    @PostMapping("/npp/calculate")
    public ApiResponse<NPPInversionResponse> calculateNPP(
            @RequestBody @Valid NPPInversionRequest request) {
        return ApiResponse.ok("NPP反演计算完成", nppInversionService.calculateNPP(request));
    }

    @GetMapping("/statistics")
    public ApiResponse<Map<String, Object>> getIndexStatistics(
            @RequestParam String areaId,
            @RequestParam String indexType) {
        return ApiResponse.ok("统计数据获取成功", remoteSensingService.getIndexStatistics(areaId, indexType));
    }
}
