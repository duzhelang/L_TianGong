package com.ecocarbon.mrv.ai;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/ai")
public class AiController {

    @Autowired
    private AiService aiService;

    @Autowired
    private AiProviderConfig config;

    @PostMapping("/chat")
    public ResponseEntity<Map<String, Object>> chat(@RequestBody ChatRequest request) {
        String response;
        if (request.getProvider() != null && !request.getProvider().isEmpty()) {
            response = aiService.chat(request.getProvider(), request.getModel(),
                    request.getSystemPrompt(), request.getMessage());
        } else {
            response = aiService.chat(request.getSystemPrompt(), request.getMessage());
        }
        return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", Map.of("response", response)
        ));
    }

    @PostMapping("/suggestions")
    public ResponseEntity<Map<String, Object>> suggestions(@RequestBody Map<String, Object> emissionData) {
        var suggestions = aiService.generateReductionSuggestions(emissionData);
        return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", suggestions
        ));
    }

    @PostMapping("/predict")
    public ResponseEntity<Map<String, Object>> predict(@RequestBody PredictionRequest request) {
        var prediction = aiService.predictEmission(request.getHistoricalData(), request.getPeriods());
        return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", prediction
        ));
    }

    @PostMapping("/anomaly")
    public ResponseEntity<Map<String, Object>> anomaly(@RequestBody Map<String, Object> data) {
        var anomalies = aiService.detectAnomalies(data);
        return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", anomalies
        ));
    }

    @PostMapping("/report")
    public ResponseEntity<Map<String, Object>> report(@RequestBody Map<String, Object> projectData) {
        String report = aiService.generateReport(projectData);
        return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", Map.of("report", report)
        ));
    }

    @GetMapping("/providers")
    public ResponseEntity<Map<String, Object>> getProviders() {
        var providers = config.getProviders().entrySet().stream()
                .map(entry -> Map.of(
                        "name", entry.getKey(),
                        "models", entry.getValue().getModels().keySet()
                ))
                .toList();
        return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", Map.of(
                        "defaultProvider", config.getDefaultProvider(),
                        "providers", providers
                )
        ));
    }

    @Data
    public static class ChatRequest {
        private String provider;
        private String model;
        private String systemPrompt;
        private String message;
    }

    @Data
    public static class PredictionRequest {
        private List<Map<String, Object>> historicalData;
        private int periods;
    }
}
