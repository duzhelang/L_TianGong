package com.ecocarbon.mrv.controller;

import com.ecocarbon.mrv.common.ApiResponse;
import com.ecocarbon.mrv.entity.MrvReport;
import com.ecocarbon.mrv.engine.report.MrvReportService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class MrvReportController {
    private final MrvReportService reportService;

    @GetMapping
    public ApiResponse<Page<MrvReport>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok(reportService.list(PageRequest.of(page, size)));
    }

    @GetMapping("/project/{projectId}")
    public ApiResponse<List<MrvReport>> getByProject(@PathVariable Long projectId) {
        return ApiResponse.ok(reportService.getByProjectId(projectId));
    }

    @GetMapping("/{id}")
    public ApiResponse<MrvReport> getById(@PathVariable Long id) {
        return ApiResponse.ok(reportService.getById(id));
    }

    @PostMapping
    public ApiResponse<MrvReport> create(@RequestBody MrvReport report) {
        return ApiResponse.ok("报告创建成功", reportService.create(report));
    }

    @PutMapping("/{id}/status")
    public ApiResponse<MrvReport> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return ApiResponse.ok("状态更新成功", reportService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        reportService.delete(id);
        return ApiResponse.ok("报告删除成功", null);
    }
}
