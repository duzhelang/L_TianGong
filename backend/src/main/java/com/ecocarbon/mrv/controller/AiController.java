package com.ecocarbon.mrv.controller;

import com.ecocarbon.mrv.ai.AiService;
import com.ecocarbon.mrv.ai.AnomalyDetection;
import com.ecocarbon.mrv.ai.CarbonReductionSuggestion;
import com.ecocarbon.mrv.ai.EmissionPrediction;
import com.ecocarbon.mrv.common.ApiResponse;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AiController {
    private final AiService aiService;

    @PostMapping("/chat")
    public ApiResponse<Map<String, String>> chat(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        String response = aiService.chat("你是EcoCarbon碳管理助手", message);
        return ApiResponse.ok(Map.of("response", response));
    }

    @PostMapping("/suggestions")
    public ApiResponse<List<CarbonReductionSuggestion>> getSuggestions(@RequestBody Map<String, Object> emissionData) {
        return ApiResponse.ok("减排建议生成成功", aiService.generateReductionSuggestions(emissionData));
    }

    @PostMapping("/predict")
    public ApiResponse<EmissionPrediction> predict(@RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> historicalData = (List<Map<String, Object>>) request.get("historicalData");
        int periods = (int) request.getOrDefault("periods", 3);
        return ApiResponse.ok("预测完成", aiService.predictEmission(historicalData, periods));
    }

    @PostMapping("/anomaly")
    public ApiResponse<List<AnomalyDetection>> detectAnomaly(@RequestBody Map<String, Object> data) {
        return ApiResponse.ok("异常检测完成", aiService.detectAnomalies(data));
    }

    @PostMapping("/report")
    public ApiResponse<Map<String, String>> generateReport(@RequestBody Map<String, Object> projectData) {
        String report = aiService.generateReport(projectData);
        return ApiResponse.ok("报告生成成功", Map.of("report", report));
    }
}
