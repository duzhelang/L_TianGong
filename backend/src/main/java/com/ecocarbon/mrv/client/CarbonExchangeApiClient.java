package com.ecocarbon.mrv.client;

import com.ecocarbon.mrv.config.ApiProperties;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class CarbonExchangeApiClient {

    private final ExternalApiClient apiClient;
    private final ApiProperties apiProperties;
    private WebClient webClient;

    public CarbonExchangeApiClient(ExternalApiClient apiClient, ApiProperties apiProperties) {
        this.apiClient = apiClient;
        this.apiProperties = apiProperties;
    }

    @PostConstruct
    public void init() {
        String baseUrl = apiProperties.getEnergy().getBaseUrl();
        if (baseUrl == null || baseUrl.isEmpty()) {
            baseUrl = "https://ets.cets.org.cn";
        }
        this.webClient = apiClient.createWebClient(baseUrl, apiProperties.getEnergy().getTimeoutSeconds());
    }

    public JsonNode getLatestPrice(String exchange, String productCode) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("exchange", exchange);
            params.put("product", productCode);
            return apiClient.get(webClient, "/api/v1/carbon/price/latest", params);
        } catch (Exception e) {
            log.warn("获取碳价失败: {}", e.getMessage());
            return null;
        }
    }

    public JsonNode getDailyPrices(String exchange, String productCode, String startDate, String endDate) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("exchange", exchange);
            params.put("product", productCode);
            params.put("start", startDate);
            params.put("end", endDate);
            return apiClient.get(webClient, "/api/v1/carbon/price/daily", params);
        } catch (Exception e) {
            log.warn("获取碳价历史失败: {}", e.getMessage());
            return null;
        }
    }

    public JsonNode getMarketSummary(String exchange) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("exchange", exchange);
            return apiClient.get(webClient, "/api/v1/carbon/market/summary", params);
        } catch (Exception e) {
            log.warn("获取市场概要失败: {}", e.getMessage());
            return null;
        }
    }
}
