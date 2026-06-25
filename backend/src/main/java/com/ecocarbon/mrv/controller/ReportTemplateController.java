package com.ecocarbon.mrv.controller;

import com.ecocarbon.mrv.common.ApiResponse;
import com.ecocarbon.mrv.entity.ReportTemplate;
import com.ecocarbon.mrv.service.ReportTemplateService;
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
@RequestMapping("/api/v1/report-templates")
@RequiredArgsConstructor
public class ReportTemplateController {
    private final ReportTemplateService reportTemplateService;

    @GetMapping
    public ApiResponse<Page<ReportTemplate>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok(reportTemplateService.list(PageRequest.of(page, size)));
    }

    @GetMapping("/type/{reportType}")
    public ApiResponse<List<ReportTemplate>> getByType(@PathVariable String reportType) {
        return ApiResponse.ok(reportTemplateService.getByReportType(reportType));
    }

    @GetMapping("/active")
    public ApiResponse<List<ReportTemplate>> getActive() {
        return ApiResponse.ok(reportTemplateService.getActiveTemplates());
    }

    @GetMapping("/{id}")
    public ApiResponse<ReportTemplate> getById(@PathVariable Long id) {
        return ApiResponse.ok(reportTemplateService.getById(id));
    }

    @GetMapping("/code/{templateCode}")
    public ApiResponse<ReportTemplate> getByCode(@PathVariable String templateCode) {
        return ApiResponse.ok(reportTemplateService.getByCode(templateCode));
    }

    @PostMapping
    public ApiResponse<ReportTemplate> create(@RequestBody ReportTemplate template) {
        return ApiResponse.ok("报告模板创建成功", reportTemplateService.create(template));
    }

    @PutMapping("/{id}")
    public ApiResponse<ReportTemplate> update(@PathVariable Long id, @RequestBody ReportTemplate template) {
        return ApiResponse.ok("报告模板更新成功", reportTemplateService.update(id, template));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        reportTemplateService.delete(id);
        return ApiResponse.ok("报告模板删除成功", null);
    }

    @GetMapping("/count/type/{reportType}")
    public ApiResponse<Long> countByType(@PathVariable String reportType) {
        return ApiResponse.ok(reportTemplateService.countByType(reportType));
    }

    @GetMapping("/count/active")
    public ApiResponse<Long> countActive() {
        return ApiResponse.ok(reportTemplateService.countActive());
    }
}
