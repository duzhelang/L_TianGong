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
public class RemoteSensingApiClient {

    private final ExternalApiClient apiClient;
    private final ApiProperties apiProperties;
    private final WebClient webClient;

    public RemoteSensingApiClient(ExternalApiClient apiClient, ApiProperties apiProperties) {
        this.apiClient = apiClient;
        this.apiProperties = apiProperties;
        this.webClient = apiClient.createWebClient(
                apiProperties.getRemoteSensing().getBaseUrl(),
                apiProperties.getRemoteSensing().getTimeoutSeconds()
        );
    }

    public JsonNode getNDVIData(double longitude, double latitude, String startDate, String endDate) {
        Map<String, String> params = new HashMap<>();
        params.put("longitude", String.valueOf(longitude));
        params.put("latitude", String.valueOf(latitude));
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        params.put("layer", "MOD13A2.061_1_km_16_days_NDVI");
        return apiClient.get(webClient, "/api/v1/point", params);
    }

    public JsonNode getNPPData(double longitude, double latitude, int year) {
        Map<String, String> params = new HashMap<>();
        params.put("longitude", String.valueOf(longitude));
        params.put("latitude", String.valueOf(latitude));
        params.put("year", String.valueOf(year));
        params.put("layer", "MOD17A3HGF.061_NPP");
        return apiClient.get(webClient, "/api/v1/point", params);
    }

    public JsonNode getLAIData(double longitude, double latitude, String startDate, String endDate) {
        Map<String, String> params = new HashMap<>();
        params.put("longitude", String.valueOf(longitude));
        params.put("latitude", String.valueOf(latitude));
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        params.put("layer", "MCD15A3H.061_Lai_500m");
        return apiClient.get(webClient, "/api/v1/point", params);
    }

    public JsonNode getLandCoverData(double longitude, double latitude, int year) {
        Map<String, String> params = new HashMap<>();
        params.put("longitude", String.valueOf(longitude));
        params.put("latitude", String.valueOf(latitude));
        params.put("year", String.valueOf(year));
        params.put("layer", "MCD12Q1.061_LC_Type1");
        return apiClient.get(webClient, "/api/v1/point", params);
    }

    public JsonNode getEVI(double longitude, double latitude, String startDate, String endDate) {
        Map<String, String> params = new HashMap<>();
        params.put("longitude", String.valueOf(longitude));
        params.put("latitude", String.valueOf(latitude));
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        params.put("layer", "MOD13A2.061_1_km_16_days_EVI");
        return apiClient.get(webClient, "/api/v1/point", params);
    }

    public JsonNode getAlbedo(double longitude, double latitude, String startDate, String endDate) {
        Map<String, String> params = new HashMap<>();
        params.put("longitude", String.valueOf(longitude));
        params.put("latitude", String.valueOf(latitude));
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        params.put("layer", "MCD43A3.061_Albedo_WSA_shortwave");
        return apiClient.get(webClient, "/api/v1/point", params);
    }
}