package com.ecocarbon.mrv.controller;

import com.ecocarbon.mrv.common.ApiResponse;
import com.ecocarbon.mrv.entity.CarbonTransaction;
import com.ecocarbon.mrv.service.CarbonTradingService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/trading")
@RequiredArgsConstructor
public class TradingController {
    private final CarbonTradingService tradingService;

    @GetMapping("/orders")
    public ApiResponse<Page<CarbonTransaction>> listOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok(tradingService.list(PageRequest.of(page, size)));
    }

    @GetMapping("/orders/{id}")
    public ApiResponse<CarbonTransaction> getOrderById(@PathVariable Long id) {
        return ApiResponse.ok(tradingService.getById(id));
    }

    @GetMapping("/orders/code/{code}")
    public ApiResponse<CarbonTransaction> getOrderByCode(@PathVariable String code) {
        return ApiResponse.ok(tradingService.getByCode(code));
    }

    @GetMapping("/orders/user/{userId}")
    public ApiResponse<Page<CarbonTransaction>> getOrdersByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok(tradingService.getByUser(userId, PageRequest.of(page, size)));
    }

    @GetMapping("/orders/asset/{assetId}")
    public ApiResponse<List<CarbonTransaction>> getOrdersByAsset(@PathVariable Long assetId) {
        return ApiResponse.ok(tradingService.getByAsset(assetId));
    }

    @PostMapping("/orders")
    public ApiResponse<CarbonTransaction> createOrder(@RequestBody CarbonTransaction order) {
        return ApiResponse.ok("订单创建成功", tradingService.createOrder(order));
    }

    @PutMapping("/orders/{id}/submit")
    public ApiResponse<CarbonTransaction> submitOrder(@PathVariable Long id) {
        return ApiResponse.ok("订单提交成功", tradingService.submitOrder(id));
    }

    @PutMapping("/orders/{id}/match")
    public ApiResponse<CarbonTransaction> matchOrder(@PathVariable Long id) {
        return ApiResponse.ok("订单撮合成功", tradingService.matchOrder(id));
    }

    @PutMapping("/orders/{id}/settle")
    public ApiResponse<CarbonTransaction> settleOrder(@PathVariable Long id) {
        return ApiResponse.ok("订单结算成功", tradingService.settleOrder(id));
    }

    @PutMapping("/orders/{id}/cancel")
    public ApiResponse<CarbonTransaction> cancelOrder(@PathVariable Long id) {
        return ApiResponse.ok("订单取消成功", tradingService.cancelOrder(id));
    }
}
