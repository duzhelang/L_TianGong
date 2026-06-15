package com.ecocarbon.mrv.controller;

import com.ecocarbon.mrv.common.ApiResponse;
import com.ecocarbon.mrv.dto.CarbonInventoryRequest;
import com.ecocarbon.mrv.service.CarbonEngineService;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/carbon")
@RequiredArgsConstructor
public class CarbonEngineController {
    private final CarbonEngineService carbonEngineService;

    @PostMapping("/inventory")
    public ApiResponse<Map<String, Object>> inventory(@RequestBody @Valid CarbonInventoryRequest request) {
        return ApiResponse.ok("核算完成", carbonEngineService.calculateInventory(request));
    }
}
