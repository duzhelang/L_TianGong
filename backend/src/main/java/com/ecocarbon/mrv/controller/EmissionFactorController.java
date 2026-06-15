package com.ecocarbon.mrv.controller;

import com.ecocarbon.mrv.common.ApiResponse;
import com.ecocarbon.mrv.dto.GreenFactorOptimizationRequest;
import com.ecocarbon.mrv.dto.GreenFactorOptimizationResponse;
import com.ecocarbon.mrv.entity.EmissionFactor;
import com.ecocarbon.mrv.service.AiGreenFactorService;
import com.ecocarbon.mrv.service.EmissionFactorService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/emission-factors")
@RequiredArgsConstructor
public class EmissionFactorController {
    private final EmissionFactorService emissionFactorService;
    private final AiGreenFactorService aiGreenFactorService;

    @GetMapping
    public ApiResponse<List<EmissionFactor>> list() {
        return ApiResponse.ok(emissionFactorService.findAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<EmissionFactor> getById(@PathVariable Long id) {
        return ApiResponse.ok(emissionFactorService.findById(id).orElse(null));
    }

    @GetMapping("/code/{factorCode}")
    public ApiResponse<EmissionFactor> getByCode(@PathVariable String factorCode) {
        return ApiResponse.ok(emissionFactorService.findByFactorCode(factorCode).orElse(null));
    }

    @GetMapping("/category/{category}")
    public ApiResponse<List<EmissionFactor>> getByCategory(@PathVariable String category) {
        return ApiResponse.ok(emissionFactorService.findByCategory(category));
    }

    @GetMapping("/gas-type/{gasType}")
    public ApiResponse<List<EmissionFactor>> getByGasType(@PathVariable String gasType) {
        return ApiResponse.ok(emissionFactorService.findByGasType(gasType));
    }

    @PostMapping
    public ApiResponse<EmissionFactor> create(@RequestBody EmissionFactor emissionFactor) {
        return ApiResponse.ok("创建成功", emissionFactorService.create(emissionFactor));
    }

    @PutMapping("/{id}")
    public ApiResponse<EmissionFactor> update(@PathVariable Long id, @RequestBody EmissionFactor emissionFactor) {
        return ApiResponse.ok("更新成功", emissionFactorService.update(id, emissionFactor));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        emissionFactorService.delete(id);
        return ApiResponse.ok("删除成功", null);
    }

    @PostMapping("/green-factor-optimization")
    public ApiResponse<GreenFactorOptimizationResponse> optimizeGreenFactor(
            @RequestBody @Valid GreenFactorOptimizationRequest request) {
        return ApiResponse.ok("AI推荐完成", aiGreenFactorService.optimizeGreenFactor(request));
    }
}
