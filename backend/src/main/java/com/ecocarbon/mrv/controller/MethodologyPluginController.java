package com.ecocarbon.mrv.controller;

import com.ecocarbon.mrv.common.ApiResponse;
import com.ecocarbon.mrv.engine.methodology.MethodologyPlugin;
import com.ecocarbon.mrv.engine.methodology.MethodologyPluginManager;
import com.ecocarbon.mrv.engine.methodology.MethodologyResult;
import com.ecocarbon.mrv.engine.methodology.ParameterDefinition;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/methodology-plugins")
@RequiredArgsConstructor
public class MethodologyPluginController {
    private final MethodologyPluginManager pluginManager;

    @GetMapping
    public ApiResponse<List<MethodologyPlugin>> list() {
        return ApiResponse.ok(pluginManager.getAllPlugins());
    }

    @GetMapping("/scene/{sceneType}")
    public ApiResponse<List<MethodologyPlugin>> getByScene(@PathVariable String sceneType) {
        return ApiResponse.ok(pluginManager.getPluginsByScene(sceneType));
    }

    @GetMapping("/{code}")
    public ApiResponse<MethodologyPlugin> getByCode(@PathVariable String code) {
        MethodologyPlugin plugin = pluginManager.getPlugin(code);
        if (plugin == null) {
            return ApiResponse.ok(null);
        }
        return ApiResponse.ok(plugin);
    }

    @GetMapping("/{code}/parameters")
    public ApiResponse<List<ParameterDefinition>> getParameters(@PathVariable String code) {
        MethodologyPlugin plugin = pluginManager.getPlugin(code);
        if (plugin == null) {
            return ApiResponse.ok(List.of());
        }
        return ApiResponse.ok(plugin.getParameterDefinitions());
    }

    @PostMapping("/{code}/calculate")
    public ApiResponse<MethodologyResult> calculate(
            @PathVariable String code,
            @RequestBody Map<String, Object> parameters) {
        MethodologyResult result = pluginManager.calculate(code, parameters);
        return ApiResponse.ok("计算完成", result);
    }
}
