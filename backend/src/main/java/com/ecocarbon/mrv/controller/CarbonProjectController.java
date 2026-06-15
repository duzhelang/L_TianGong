package com.ecocarbon.mrv.controller;

import com.ecocarbon.mrv.common.ApiResponse;
import com.ecocarbon.mrv.dto.CreateProjectRequest;
import com.ecocarbon.mrv.entity.CarbonProject;
import com.ecocarbon.mrv.service.CarbonProjectService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class CarbonProjectController {
    private final CarbonProjectService projectService;

    @GetMapping
    public ApiResponse<List<CarbonProject>> list() {
        return ApiResponse.ok(projectService.list());
    }

    @PostMapping
    public ApiResponse<CarbonProject> create(@RequestBody @Valid CreateProjectRequest request) {
        return ApiResponse.ok("创建成功", projectService.create(request));
    }
}
