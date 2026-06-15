package com.ecocarbon.mrv.controller;

import com.ecocarbon.mrv.common.ApiResponse;
import com.ecocarbon.mrv.dto.MethodologyConfigRequest;
import com.ecocarbon.mrv.entity.MethodologyConfig;
import com.ecocarbon.mrv.service.MethodologyService;
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
@RequestMapping("/api/v1/methodologies")
@RequiredArgsConstructor
public class MethodologyController {
    private final MethodologyService methodologyService;

    @GetMapping
    public ApiResponse<List<MethodologyConfig>> list() {
        return ApiResponse.ok(methodologyService.findAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<MethodologyConfig> getById(@PathVariable Long id) {
        return ApiResponse.ok(methodologyService.findById(id).orElse(null));
    }

    @GetMapping("/code/{methodologyCode}")
    public ApiResponse<MethodologyConfig> getByCode(@PathVariable String methodologyCode) {
        return ApiResponse.ok(methodologyService.findByMethodologyCode(methodologyCode).orElse(null));
    }

    @GetMapping("/scene/{scene}")
    public ApiResponse<List<MethodologyConfig>> getByScene(@PathVariable String scene) {
        return ApiResponse.ok(methodologyService.findByScene(scene));
    }

    @PostMapping
    public ApiResponse<MethodologyConfig> create(@RequestBody @Valid MethodologyConfigRequest request) {
        MethodologyConfig config = new MethodologyConfig();
        config.setMethodologyCode(request.methodologyCode());
        config.setMethodologyName(request.methodologyName());
        config.setVersion(request.version());
        config.setDescription(request.description());
        config.setApplicableScenes(request.applicableScenes());
        config.setCalculationFormula(request.calculationFormula());
        config.setParametersConfig(request.parametersConfig());
        config.setReferenceStandard(request.referenceStandard());
        return ApiResponse.ok("创建成功", methodologyService.create(config));
    }

    @PutMapping("/{id}")
    public ApiResponse<MethodologyConfig> update(@PathVariable Long id,
                                                 @RequestBody @Valid MethodologyConfigRequest request) {
        MethodologyConfig config = new MethodologyConfig();
        config.setMethodologyName(request.methodologyName());
        config.setVersion(request.version());
        config.setDescription(request.description());
        config.setApplicableScenes(request.applicableScenes());
        config.setCalculationFormula(request.calculationFormula());
        config.setParametersConfig(request.parametersConfig());
        config.setReferenceStandard(request.referenceStandard());
        return ApiResponse.ok("更新成功", methodologyService.update(id, config));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        methodologyService.delete(id);
        return ApiResponse.ok("删除成功", null);
    }
}
