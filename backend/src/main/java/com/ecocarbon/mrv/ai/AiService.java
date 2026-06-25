package com.ecocarbon.mrv.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AiService {

    @Autowired
    private AiProviderConfig config;

    @Autowired
    private AiClient aiClient;

    @Autowired
    private ObjectMapper objectMapper;

    public String chat(String systemPrompt, String userMessage) {
        if (!config.isEnabled()) {
            return "AI功能未启用，请在配置中设置 app.ai.enabled=true";
        }
        try {
            return callLLM(systemPrompt, userMessage);
        } catch (Exception e) {
            log.error("AI调用失败: {}", e.getMessage());
            return "AI服务暂时不可用，请稍后再试";
        }
    }

    public String chat(String provider, String model, String systemPrompt, String userMessage) {
        if (!config.isEnabled()) {
            return "AI功能未启用，请在配置中设置 app.ai.enabled=true";
        }
        try {
            return callLLMWithProvider(provider, model, systemPrompt, userMessage);
        } catch (Exception e) {
            log.error("AI调用失败: {}", e.getMessage());
            return "AI服务暂时不可用，请稍后再试";
        }
    }

    public List<CarbonReductionSuggestion> generateReductionSuggestions(Map<String, Object> emissionData) {
        String prompt = buildReductionPrompt(emissionData);
        String systemPrompt = "你是碳管理专家，请根据提供的碳排放数据，生成具体的减排建议。每条建议包含：类别、标题、描述、潜在减排量、实施难度。";
        String response = chat(systemPrompt, prompt);
        return parseSuggestions(response);
    }

    public EmissionPrediction predictEmission(List<Map<String, Object>> historicalData, int periods) {
        String prompt = buildPredictionPrompt(historicalData, periods);
        String systemPrompt = "你是碳排放预测专家，请根据历史数据预测未来碳排放趋势。返回JSON格式的预测结果。";
        String response = chat(systemPrompt, prompt);
        return parsePrediction(response);
    }

    public List<AnomalyDetection> detectAnomalies(Map<String, Object> data) {
        String prompt = buildAnomalyPrompt(data);
        String systemPrompt = "你是碳排放异常检测专家，请分析数据中的异常情况。返回JSON格式的异常列表。";
        String response = chat(systemPrompt, prompt);
        return parseAnomalies(response);
    }

    public String generateReport(Map<String, Object> projectData) {
        String prompt = buildReportPrompt(projectData);
        String systemPrompt = "你是碳管理报告撰写专家，请根据项目数据生成专业的碳排放分析报告。";
        return chat(systemPrompt, prompt);
    }

    private String callLLM(String systemPrompt, String userMessage) {
        String provider = config.getDefaultProvider();
        return callLLMWithProvider(provider, null, systemPrompt, userMessage);
    }

    private String callLLMWithProvider(String providerName, String modelName,
                                        String systemPrompt, String userMessage) {
        Map<String, AiProviderConfig.ProviderConfig> providers = config.getProviders();
        if (providers == null || providers.isEmpty()) {
            log.warn("未配置AI提供商，使用本地模式");
            return generateLocalResponse(userMessage);
        }
        AiProviderConfig.ProviderConfig provider = providers.get(providerName);
        if (provider == null) {
            log.warn("未找到AI提供商: {}，使用本地模式", providerName);
            return generateLocalResponse(userMessage);
        }

        String model = modelName != null ? modelName : provider.getDefaultModel();
        if (model == null || model.isEmpty()) {
            log.warn("提供商 {} 未配置默认模型", providerName);
            return generateLocalResponse(userMessage);
        }

        String response = aiClient.callChatCompletion(
                provider.getBaseUrl(),
                provider.getApiKey(),
                model,
                systemPrompt,
                userMessage
        );

        if (response != null) {
            return response;
        }

        log.warn("AI API调用失败，使用本地模式");
        return generateLocalResponse(userMessage);
    }

    private String generateLocalResponse(String message) {
        if (message.contains("减排") || message.contains("建议")) {
            return "基于您的碳排放数据，建议：1）优化能源结构，增加可再生能源比例；2）提升设备能效，减少能源浪费；3）推广低碳技术应用。";
        }
        if (message.contains("预测") || message.contains("趋势")) {
            return "根据历史数据分析，预计未来碳排放将保持稳定或小幅下降趋势。";
        }
        return "我是EcoCarbon碳管理助手，可以为您提供碳核算、减排建议、排放预测等服务。";
    }

    private String buildReductionPrompt(Map<String, Object> data) {
        return String.format("""
            碳排放数据：%s
            
            请提供减排建议，必须按照以下JSON格式返回：
            {
              "suggestions": [
                {
                  "category": "类别",
                  "title": "建议标题",
                  "description": "详细描述",
                  "potentialReduction": 数值,
                  "unit": "tCO2e",
                  "difficulty": "高/中/低",
                  "paybackPeriod": "投资回收期"
                }
              ]
            }
            """, data.toString());
    }

    private String buildPredictionPrompt(List<Map<String, Object>> data, int periods) {
        return String.format("""
            历史数据：%s
            
            请预测未来%d期的碳排放，必须按照以下JSON格式返回：
            {
              "predictions": [
                {
                  "period": "时期",
                  "value": 数值,
                  "confidence": 置信度
                }
              ],
              "trend": "趋势描述"
            }
            """, data.toString(), periods);
    }

    private String buildAnomalyPrompt(Map<String, Object> data) {
        return String.format("""
            监测数据：%s
            
            请检测数据中的异常情况，必须按照以下JSON格式返回：
            {
              "anomalies": [
                {
                  "timestamp": "时间戳",
                  "metric": "指标名称",
                  "actualValue": 实际值,
                  "expectedValue": 预期值,
                  "deviation": 偏差值,
                  "severity": "严重程度",
                  "description": "异常描述"
                }
              ]
            }
            """, data.toString());
    }

    private String buildReportPrompt(Map<String, Object> data) {
        return "项目数据：" + data.toString() + "\n请生成碳排放分析报告。";
    }

    /**
     * 解析AI返回的减排建议
     * 支持JSON格式和文本格式的降级处理
     */
    private List<CarbonReductionSuggestion> parseSuggestions(String response) {
        if (response == null || response.isEmpty()) {
            log.warn("AI响应为空，返回默认建议");
            return getDefaultSuggestions();
        }

        try {
            // 尝试解析JSON格式
            JsonNode root = objectMapper.readTree(response);
            if (root.isArray()) {
                return parseSuggestionsFromArray(root);
            } else if (root.has("suggestions")) {
                return parseSuggestionsFromArray(root.get("suggestions"));
            } else if (root.has("data")) {
                return parseSuggestionsFromArray(root.get("data"));
            }
            
            // 如果JSON格式不符合预期，尝试解析文本格式
            return parseSuggestionsFromText(response);
            
        } catch (JsonProcessingException e) {
            log.warn("AI响应JSON解析失败，尝试文本解析: {}", e.getMessage());
            return parseSuggestionsFromText(response);
        } catch (Exception e) {
            log.error("解析AI建议失败: {}", e.getMessage(), e);
            return getDefaultSuggestions();
        }
    }

    /**
     * 从JSON数组解析减排建议
     */
    private List<CarbonReductionSuggestion> parseSuggestionsFromArray(JsonNode arrayNode) {
        List<CarbonReductionSuggestion> suggestions = new ArrayList<>();
        for (JsonNode item : arrayNode) {
            try {
                CarbonReductionSuggestion suggestion = CarbonReductionSuggestion.builder()
                        .category(getTextValue(item, "category", "通用"))
                        .title(getTextValue(item, "title", "未命名建议"))
                        .description(getTextValue(item, "description", ""))
                        .potentialReduction(getDoubleValue(item, "potentialReduction", 0.0))
                        .unit(getTextValue(item, "unit", "tCO2e"))
                        .difficulty(getTextValue(item, "difficulty", "中"))
                        .paybackPeriod(getTextValue(item, "paybackPeriod", ""))
                        .build();
                suggestions.add(suggestion);
            } catch (Exception e) {
                log.warn("解析单条建议失败: {}", e.getMessage());
            }
        }
        return suggestions.isEmpty() ? getDefaultSuggestions() : suggestions;
    }

    /**
     * 从文本格式解析减排建议
     */
    private List<CarbonReductionSuggestion> parseSuggestionsFromText(String text) {
        List<CarbonReductionSuggestion> suggestions = new ArrayList<>();
        String[] lines = text.split("\n");
        
        for (String line : lines) {
            if (line.contains("建议") || line.contains("推荐") || line.contains("优化")) {
                suggestions.add(CarbonReductionSuggestion.builder()
                        .category("文本解析")
                        .title(line.trim())
                        .description("从AI文本响应中提取")
                        .potentialReduction(0.0)
                        .difficulty("未知")
                        .build());
            }
        }
        
        return suggestions.isEmpty() ? getDefaultSuggestions() : suggestions;
    }

    /**
     * 获取默认的减排建议
     */
    private List<CarbonReductionSuggestion> getDefaultSuggestions() {
        return List.of(
                CarbonReductionSuggestion.builder()
                        .category("默认建议")
                        .title("优化能源结构")
                        .description("建议增加可再生能源比例，减少化石能源使用")
                        .potentialReduction(50.0)
                        .difficulty("中")
                        .build()
        );
    }

    /**
     * 解析AI返回的排放预测
     * 支持JSON格式和文本格式的降级处理
     */
    private EmissionPrediction parsePrediction(String response) {
        if (response == null || response.isEmpty()) {
            log.warn("AI响应为空，返回默认预测");
            return getDefaultPrediction();
        }

        try {
            JsonNode root = objectMapper.readTree(response);
            
            // 支持多种JSON格式
            JsonNode predictionsNode = null;
            if (root.has("predictions")) {
                predictionsNode = root.get("predictions");
            } else if (root.has("data")) {
                predictionsNode = root.get("data");
            } else if (root.isArray()) {
                predictionsNode = root;
            }

            if (predictionsNode != null && predictionsNode.isArray()) {
                List<Map<String, Object>> predictions = new ArrayList<>();
                for (JsonNode item : predictionsNode) {
                    Map<String, Object> prediction = new HashMap<>();
                    prediction.put("period", getTextValue(item, "period", "未知"));
                    prediction.put("value", getDoubleValue(item, "value", 0.0));
                    prediction.put("confidence", getDoubleValue(item, "confidence", 0.5));
                    predictions.add(prediction);
                }

                return EmissionPrediction.builder()
                        .predictions(predictions)
                        .trend(getTextValue(root, "trend", "稳定"))
                        .confidence(getTextValue(root, "confidence", "中"))
                        .build();
            }

            // 文本格式降级处理
            return parsePredictionFromText(response);

        } catch (JsonProcessingException e) {
            log.warn("AI预测JSON解析失败，尝试文本解析: {}", e.getMessage());
            return parsePredictionFromText(response);
        } catch (Exception e) {
            log.error("解析AI预测失败: {}", e.getMessage(), e);
            return getDefaultPrediction();
        }
    }

    /**
     * 从文本格式解析排放预测
     */
    private EmissionPrediction parsePredictionFromText(String text) {
        return EmissionPrediction.builder()
                .predictions(List.of(
                        Map.of("period", "下期", "value", 1000.0, "confidence", 0.6, "source", "文本解析")
                ))
                .trend("基于文本分析")
                .confidence("低")
                .build();
    }

    /**
     * 获取默认的排放预测
     */
    private EmissionPrediction getDefaultPrediction() {
        return EmissionPrediction.builder()
                .predictions(List.of(
                        Map.of("period", "2025", "value", 1000.0, "confidence", 0.5, "source", "默认值")
                ))
                .trend("稳定")
                .confidence("低")
                .build();
    }

    /**
     * 解析AI返回的异常检测结果
     * 支持JSON格式和文本格式的降级处理
     */
    private List<AnomalyDetection> parseAnomalies(String response) {
        if (response == null || response.isEmpty()) {
            log.warn("AI响应为空，返回空异常列表");
            return List.of();
        }

        try {
            JsonNode root = objectMapper.readTree(response);
            
            JsonNode anomaliesNode = null;
            if (root.has("anomalies")) {
                anomaliesNode = root.get("anomalies");
            } else if (root.has("data")) {
                anomaliesNode = root.get("data");
            } else if (root.isArray()) {
                anomaliesNode = root;
            }

            if (anomaliesNode != null && anomaliesNode.isArray()) {
                List<AnomalyDetection> anomalies = new ArrayList<>();
                for (JsonNode item : anomaliesNode) {
                    try {
                        AnomalyDetection anomaly = AnomalyDetection.builder()
                                .timestamp(getTextValue(item, "timestamp", LocalDateTime.now().toString()))
                                .metric(getTextValue(item, "metric", "未知指标"))
                                .actualValue(getDoubleValue(item, "actualValue", 0.0))
                                .expectedValue(getDoubleValue(item, "expectedValue", 0.0))
                                .deviation(getDoubleValue(item, "deviation", 0.0))
                                .severity(getTextValue(item, "severity", "中"))
                                .description(getTextValue(item, "description", ""))
                                .build();
                        anomalies.add(anomaly);
                    } catch (Exception e) {
                        log.warn("解析单条异常记录失败: {}", e.getMessage());
                    }
                }
                return anomalies;
            }

            // 文本格式降级处理
            return parseAnomaliesFromText(response);

        } catch (JsonProcessingException e) {
            log.warn("AI异常检测JSON解析失败，尝试文本解析: {}", e.getMessage());
            return parseAnomaliesFromText(response);
        } catch (Exception e) {
            log.error("解析AI异常检测失败: {}", e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * 从文本格式解析异常检测结果
     */
    private List<AnomalyDetection> parseAnomaliesFromText(String text) {
        List<AnomalyDetection> anomalies = new ArrayList<>();
        if (text.toLowerCase().contains("异常") || text.toLowerCase().contains("anomaly")) {
            anomalies.add(AnomalyDetection.builder()
                    .timestamp(LocalDateTime.now().toString())
                    .metric("文本检测")
                    .actualValue(0.0)
                    .expectedValue(0.0)
                    .deviation(0.0)
                    .severity("低")
                    .description("从AI文本响应中发现异常提及")
                    .build());
        }
        return anomalies;
    }

    /**
     * 辅助方法：安全获取文本值
     */
    private String getTextValue(JsonNode node, String fieldName, String defaultValue) {
        if (node.has(fieldName)) {
            return node.get(fieldName).asText(defaultValue);
        }
        return defaultValue;
    }

    /**
     * 辅助方法：安全获取数值
     */
    private Double getDoubleValue(JsonNode node, String fieldName, Double defaultValue) {
        if (node.has(fieldName)) {
            return node.get(fieldName).asDouble(defaultValue);
        }
        return defaultValue;
    }
}
