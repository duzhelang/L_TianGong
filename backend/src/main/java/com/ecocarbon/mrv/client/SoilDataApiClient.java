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
public class SoilDataApiClient {

    private final ExternalApiClient apiClient;
    private final ApiProperties apiProperties;
    private final WebClient webClient;

    public SoilDataApiClient(ExternalApiClient apiClient, ApiProperties apiProperties) {
        this.apiClient = apiClient;
        this.apiProperties = apiProperties;
        this.webClient = apiClient.createWebClient(
                apiProperties.getSoil().getBaseUrl(),
                apiProperties.getSoil().getTimeoutSeconds()
        );
    }

    public JsonNode getSoilProperties(double longitude, double latitude, int depth) {
        Map<String, String> params = new HashMap<>();
        params.put("lon", String.valueOf(longitude));
        params.put("lat", String.valueOf(latitude));
        params.put("depth", depth + "cm");
        return apiClient.get(webClient, "/soilgrids/v2.0/properties/query", params);
    }

    public JsonNode getSoilOrganicCarbon(double longitude, double latitude) {
        Map<String, String> params = new HashMap<>();
        params.put("lon", String.valueOf(longitude));
        params.put("lat", String.valueOf(latitude));
        params.put("property", "ocd");
        params.put("depth", "0-30cm");
        return apiClient.get(webClient, "/soilgrids/v2.0/properties/query", params);
    }

    public JsonNode getSoilType(double longitude, double latitude) {
        Map<String, String> params = new HashMap<>();
        params.put("lon", String.valueOf(longitude));
        params.put("lat", String.valueOf(latitude));
        return apiClient.get(webClient, "/soilgrids/v2.0/classification/query", params);
    }

    public JsonNode getSoilTexture(double longitude, double latitude) {
        Map<String, String> params = new HashMap<>();
        params.put("lon", String.valueOf(longitude));
        params.put("lat", String.valueOf(latitude));
        params.put("property", "clay,sand,silt");
        params.put("depth", "0-30cm");
        return apiClient.get(webClient, "/soilgrids/v2.0/properties/query", params);
    }

    public JsonNode getSoilPH(double longitude, double latitude) {
        Map<String, String> params = new HashMap<>();
        params.put("lon", String.valueOf(longitude));
        params.put("lat", String.valueOf(latitude));
        params.put("property", "phh2o");
        params.put("depth", "0-30cm");
        return apiClient.get(webClient, "/soilgrids/v2.0/properties/query", params);
    }

    public JsonNode getBulkDensity(double longitude, double latitude) {
        Map<String, String> params = new HashMap<>();
        params.put("lon", String.valueOf(longitude));
        params.put("lat", String.valueOf(latitude));
        params.put("property", "bdod");
        params.put("depth", "0-30cm");
        return apiClient.get(webClient, "/soilgrids/v2.0/properties/query", params);
    }
}