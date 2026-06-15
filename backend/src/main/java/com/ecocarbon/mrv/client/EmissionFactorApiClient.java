package com.ecocarbon.mrv.client;

import com.ecocarbon.mrv.config.ApiProperties;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class EmissionFactorApiClient {

    private final ExternalApiClient apiClient;
    private final ApiProperties apiProperties;
    private final WebClient webClient;

    public EmissionFactorApiClient(ExternalApiClient apiClient, ApiProperties apiProperties) {
        this.apiClient = apiClient;
        this.apiProperties = apiProperties;
        this.webClient = apiClient.createWebClientWithApiKey(
                apiProperties.getEmissionFactor().getBaseUrl(),
                apiProperties.getEmissionFactor().getApiKey(),
                apiProperties.getEmissionFactor().getTimeoutSeconds()
        );
    }

    public JsonNode searchEmissionFactors(String keyword, String category) {
        Map<String, String> params = new HashMap<>();
        params.put("keyword", keyword);
        if (category != null) {
            params.put("category", category);
        }
        return apiClient.get(webClient, "/api/v1/emission-factors/search", params);
    }

    public JsonNode getEmissionFactorById(String factorId) {
        return apiClient.get(webClient, "/api/v1/emission-factors/" + factorId, null);
    }

    public JsonNode getEmissionFactorsBySource(String source) {
        Map<String, String> params = new HashMap<>();
        params.put("source", source);
        return apiClient.get(webClient, "/api/v1/emission-factors", params);
    }

    public JsonNode getElectricityEmissionFactor(String region, int year) {
        Map<String, String> params = new HashMap<>();
        params.put("region", region);
        params.put("year", String.valueOf(year));
        return apiClient.get(webClient, "/api/v1/emission-factors/electricity", params);
    }

    public JsonNode getFuelEmissionFactor(String fuelType) {
        Map<String, String> params = new HashMap<>();
        params.put("fuelType", fuelType);
        return apiClient.get(webClient, "/api/v1/emission-factors/fuel", params);
    }

    public JsonNode getIndustrialProcessFactor(String processType) {
        Map<String, String> params = new HashMap<>();
        params.put("processType", processType);
        return apiClient.get(webClient, "/api/v1/emission-factors/industrial", params);
    }
}