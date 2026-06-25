package com.ecocarbon.mrv.controller;

import com.ecocarbon.mrv.common.ApiResponse;
import com.ecocarbon.mrv.entity.ActivityData;
import com.ecocarbon.mrv.service.ActivityDataService;
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
@RequestMapping("/api/v1/activity-data")
@RequiredArgsConstructor
public class ActivityDataController {
    private final ActivityDataService activityDataService;

    @GetMapping
    public ApiResponse<Page<ActivityData>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok(activityDataService.list(PageRequest.of(page, size)));
    }

    @GetMapping("/project/{projectId}")
    public ApiResponse<List<ActivityData>> getByProject(@PathVariable Long projectId) {
        return ApiResponse.ok(activityDataService.getByProjectId(projectId));
    }

    @GetMapping("/type/{dataType}")
    public ApiResponse<List<ActivityData>> getByDataType(@PathVariable String dataType) {
        return ApiResponse.ok(activityDataService.getByDataType(dataType));
    }

    @GetMapping("/category/{activityCategory}")
    public ApiResponse<List<ActivityData>> getByCategory(@PathVariable String activityCategory) {
        return ApiResponse.ok(activityDataService.getByActivityCategory(activityCategory));
    }

    @GetMapping("/project/{projectId}/type/{dataType}")
    public ApiResponse<List<ActivityData>> getByProjectAndType(
            @PathVariable Long projectId,
            @PathVariable String dataType) {
        return ApiResponse.ok(activityDataService.getByProjectIdAndDataType(projectId, dataType));
    }

    @GetMapping("/{id}")
    public ApiResponse<ActivityData> getById(@PathVariable Long id) {
        return ApiResponse.ok(activityDataService.getById(id));
    }

    @PostMapping
    public ApiResponse<ActivityData> create(@RequestBody ActivityData activityData) {
        return ApiResponse.ok("活动数据创建成功", activityDataService.create(activityData));
    }

    @PutMapping("/{id}")
    public ApiResponse<ActivityData> update(@PathVariable Long id, @RequestBody ActivityData activityData) {
        return ApiResponse.ok("活动数据更新成功", activityDataService.update(id, activityData));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        activityDataService.delete(id);
        return ApiResponse.ok("活动数据删除成功", null);
    }

    @PostMapping("/batch")
    public ApiResponse<List<ActivityData>> batchCreate(@RequestBody List<ActivityData> activityDataList) {
        return ApiResponse.ok("批量创建成功", activityDataService.batchCreate(activityDataList));
    }

    @GetMapping("/count/project/{projectId}")
    public ApiResponse<Long> countByProject(@PathVariable Long projectId) {
        return ApiResponse.ok(activityDataService.countByProjectId(projectId));
    }

    @GetMapping("/count/type/{dataType}")
    public ApiResponse<Long> countByType(@PathVariable String dataType) {
        return ApiResponse.ok(activityDataService.countByDataType(dataType));
    }
}
