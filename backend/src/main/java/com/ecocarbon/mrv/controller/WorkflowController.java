package com.ecocarbon.mrv.controller;

import com.ecocarbon.mrv.common.ApiResponse;
import com.ecocarbon.mrv.entity.WorkflowLog;
import com.ecocarbon.mrv.entity.WorkflowTask;
import com.ecocarbon.mrv.security.SecurityUtils;
import com.ecocarbon.mrv.service.WorkflowService;
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
@RequestMapping("/api/v1/workflows")
@RequiredArgsConstructor
public class WorkflowController {
    private final WorkflowService workflowService;

    @GetMapping
    public ApiResponse<Page<WorkflowTask>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok(workflowService.listTasks(PageRequest.of(page, size)));
    }

    @GetMapping("/project/{projectId}")
    public ApiResponse<List<WorkflowTask>> getByProject(@PathVariable Long projectId) {
        return ApiResponse.ok(workflowService.getTasksByProject(projectId));
    }

    @GetMapping("/status/{status}")
    public ApiResponse<List<WorkflowTask>> getByStatus(@PathVariable String status) {
        return ApiResponse.ok(workflowService.getTasksByStatus(status));
    }

    @GetMapping("/assigned/{assignedTo}")
    public ApiResponse<List<WorkflowTask>> getByAssignedTo(@PathVariable Long assignedTo) {
        return ApiResponse.ok(workflowService.getTasksByAssignedTo(assignedTo));
    }

    @GetMapping("/{id}")
    public ApiResponse<WorkflowTask> getById(@PathVariable Long id) {
        return ApiResponse.ok(workflowService.getTaskById(id));
    }

    @PostMapping
    public ApiResponse<WorkflowTask> create(@RequestBody WorkflowTask task) {
        return ApiResponse.ok("工作流任务创建成功", workflowService.createTask(task));
    }

    @PutMapping("/{id}")
    public ApiResponse<WorkflowTask> update(@PathVariable Long id, @RequestBody WorkflowTask task) {
        return ApiResponse.ok("工作流任务更新成功", workflowService.updateTask(id, task));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        workflowService.deleteTask(id);
        return ApiResponse.ok("工作流任务删除成功", null);
    }

    @PostMapping("/{id}/submit")
    public ApiResponse<WorkflowTask> submit(
            @PathVariable Long id,
            @RequestParam(required = false) String comment) {
        Long operatorId = SecurityUtils.getCurrentUserId();
        String operatorName = SecurityUtils.getCurrentUsername();
        return ApiResponse.ok("任务提交成功", workflowService.submitTask(id, operatorId, operatorName, comment));
    }

    @PostMapping("/{id}/approve")
    public ApiResponse<WorkflowTask> approve(
            @PathVariable Long id,
            @RequestParam(required = false) String comment) {
        Long operatorId = SecurityUtils.getCurrentUserId();
        String operatorName = SecurityUtils.getCurrentUsername();
        return ApiResponse.ok("任务审批通过", workflowService.approveTask(id, operatorId, operatorName, comment));
    }

    @PostMapping("/{id}/reject")
    public ApiResponse<WorkflowTask> reject(
            @PathVariable Long id,
            @RequestParam(required = false) String comment) {
        Long operatorId = SecurityUtils.getCurrentUserId();
        String operatorName = SecurityUtils.getCurrentUsername();
        return ApiResponse.ok("任务审批驳回", workflowService.rejectTask(id, operatorId, operatorName, comment));
    }

    @PostMapping("/{id}/complete")
    public ApiResponse<WorkflowTask> complete(
            @PathVariable Long id,
            @RequestParam(required = false) String comment) {
        Long operatorId = SecurityUtils.getCurrentUserId();
        String operatorName = SecurityUtils.getCurrentUsername();
        return ApiResponse.ok("任务完成", workflowService.completeTask(id, operatorId, operatorName, comment));
    }

    @PostMapping("/{id}/cancel")
    public ApiResponse<WorkflowTask> cancel(
            @PathVariable Long id,
            @RequestParam(required = false) String comment) {
        Long operatorId = SecurityUtils.getCurrentUserId();
        String operatorName = SecurityUtils.getCurrentUsername();
        return ApiResponse.ok("任务取消", workflowService.cancelTask(id, operatorId, operatorName, comment));
    }

    @GetMapping("/{id}/logs")
    public ApiResponse<List<WorkflowLog>> getTaskLogs(@PathVariable Long id) {
        return ApiResponse.ok(workflowService.getTaskLogs(id));
    }

    @GetMapping("/count/status/{status}")
    public ApiResponse<Long> countByStatus(@PathVariable String status) {
        return ApiResponse.ok(workflowService.countByStatus(status));
    }

    @GetMapping("/count/project/{projectId}")
    public ApiResponse<Long> countByProject(@PathVariable Long projectId) {
        return ApiResponse.ok(workflowService.countByProject(projectId));
    }
}
