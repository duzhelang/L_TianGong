package com.ecocarbon.mrv.controller;

import com.ecocarbon.mrv.common.ApiResponse;
import com.ecocarbon.mrv.entity.CarbonPrice;
import com.ecocarbon.mrv.service.CarbonPriceSyncService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/carbon-market")
@RequiredArgsConstructor
public class CarbonPriceController {
    private final CarbonPriceSyncService carbonPriceSyncService;

    @PostMapping("/sync")
    public ApiResponse<List<CarbonPrice>> syncLatestPrices() {
        return ApiResponse.ok("碳价同步成功", carbonPriceSyncService.syncLatestPrices());
    }

    @GetMapping("/latest")
    public ApiResponse<List<CarbonPrice>> getLatestPrices() {
        return ApiResponse.ok(carbonPriceSyncService.getLatestFromDb());
    }

    @GetMapping("/history")
    public ApiResponse<List<CarbonPrice>> getHistory(
            @RequestParam String exchange,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ApiResponse.ok(carbonPriceSyncService.getByExchangeAndDateRange(exchange, startDate, endDate));
    }

    @PostMapping("/sync/daily")
    public ApiResponse<List<CarbonPrice>> syncDailyPrices(
            @RequestParam String exchange,
            @RequestParam String productCode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ApiResponse.ok("日碳价同步成功", carbonPriceSyncService.syncDailyPrices(
                exchange, productCode, startDate.toString(), endDate.toString()));
    }
}
