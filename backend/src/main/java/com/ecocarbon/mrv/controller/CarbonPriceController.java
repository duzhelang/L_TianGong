package com.ecocarbon.mrv.controller;

import com.ecocarbon.mrv.common.ApiResponse;
import com.ecocarbon.mrv.entity.CarbonPrice;
import com.ecocarbon.mrv.service.CarbonPriceService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/carbon/prices")
@RequiredArgsConstructor
public class CarbonPriceController {
    private final CarbonPriceService priceService;

    @GetMapping
    public ApiResponse<List<CarbonPrice>> getByExchange(@RequestParam String exchange) {
        return ApiResponse.ok(priceService.getByExchange(exchange));
    }

    @GetMapping("/history")
    public ApiResponse<List<CarbonPrice>> getHistory(
            @RequestParam String exchange,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ApiResponse.ok(priceService.getByExchangeAndDateRange(exchange, start, end));
    }

    @GetMapping("/latest")
    public ApiResponse<CarbonPrice> getLatest(
            @RequestParam String exchange,
            @RequestParam String productCode) {
        return ApiResponse.ok(priceService.getLatestPrice(exchange, productCode));
    }

    @PostMapping
    public ApiResponse<CarbonPrice> create(@RequestBody CarbonPrice price) {
        return ApiResponse.ok("碳价数据创建成功", priceService.create(price));
    }

    @PostMapping("/batch")
    public ApiResponse<List<CarbonPrice>> createBatch(@RequestBody List<CarbonPrice> prices) {
        return ApiResponse.ok("碳价数据批量创建成功", priceService.createBatch(prices));
    }
}
