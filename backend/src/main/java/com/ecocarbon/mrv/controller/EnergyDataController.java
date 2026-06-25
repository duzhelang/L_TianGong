package com.ecocarbon.mrv.controller;

import com.ecocarbon.mrv.common.ApiResponse;
import com.ecocarbon.mrv.entity.EnergyData;
import com.ecocarbon.mrv.service.EnergyDataService;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/energy")
@RequiredArgsConstructor
public class EnergyDataController {
    private final EnergyDataService energyDataService;

    @GetMapping
    public ApiResponse<Page<EnergyData>> list(
            @RequestParam Long projectId,
            @RequestParam(required = false) String energyType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok(energyDataService.list(projectId, energyType, startDate, endDate,
                PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    public ApiResponse<EnergyData> getById(@PathVariable Long id) {
        return ApiResponse.ok(energyDataService.getById(id));
    }

    @PostMapping
    public ApiResponse<EnergyData> create(@RequestBody EnergyData data) {
        return ApiResponse.ok("能耗数据创建成功", energyDataService.create(data));
    }

    @PostMapping("/batch")
    public ApiResponse<List<EnergyData>> createBatch(@RequestBody List<EnergyData> dataList) {
        return ApiResponse.ok("批量创建成功", energyDataService.createBatch(dataList));
    }

    @PostMapping("/calculate")
    public ApiResponse<EnergyData> calculate(
            @RequestParam Long projectId,
            @RequestParam String energyType,
            @RequestParam String energyCategory,
            @RequestParam Double consumption,
            @RequestParam String unit,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate recordDate,
            @RequestParam(required = false) String source) {
        return ApiResponse.ok("计算并保存成功", energyDataService.calculateAndSave(
                projectId, energyType, energyCategory, consumption, unit, recordDate, source));
    }

    @PutMapping("/{id}")
    public ApiResponse<EnergyData> update(@PathVariable Long id, @RequestBody EnergyData data) {
        return ApiResponse.ok("更新成功", energyDataService.update(id, data));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        energyDataService.delete(id);
        return ApiResponse.ok("删除成功", null);
    }

    @GetMapping("/statistics")
    public ApiResponse<Map<String, Object>> getStatistics(
            @RequestParam Long projectId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ApiResponse.ok(energyDataService.getStatistics(projectId, startDate, endDate));
    }
}
