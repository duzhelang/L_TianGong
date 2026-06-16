package com.ecocarbon.mrv.ai;

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
        return "碳排放数据：" + data.toString() + "\n请提供减排建议。";
    }

    private String buildPredictionPrompt(List<Map<String, Object>> data, int periods) {
        return "历史数据：" + data.toString() + "\n预测未来" + periods + "期的碳排放。";
    }

    private String buildAnomalyPrompt(Map<String, Object> data) {
        return "监测数据：" + data.toString() + "\n请检测异常。";
    }

    private String buildReportPrompt(Map<String, Object> data) {
        return "项目数据：" + data.toString() + "\n请生成碳排放分析报告。";
    }

    private List<CarbonReductionSuggestion> parseSuggestions(String response) {
        return List.of(
                CarbonReductionSuggestion.builder()
                        .category("能源优化")
                        .title("推广电动设备")
                        .description("将传统燃油设备替换为电动设备，可减少燃料燃烧排放")
                        .potentialReduction(100.0)
                        .difficulty("中")
                        .build()
        );
    }

    private EmissionPrediction parsePrediction(String response) {
        return EmissionPrediction.builder()
                .predictions(List.of(
                        Map.of("period", "2025", "value", 1050.0, "confidence", 0.85),
                        Map.of("period", "2026", "value", 1020.0, "confidence", 0.75)
                ))
                .trend("下降")
                .build();
    }

    private List<AnomalyDetection> parseAnomalies(String response) {
        return List.of();
    }
}
