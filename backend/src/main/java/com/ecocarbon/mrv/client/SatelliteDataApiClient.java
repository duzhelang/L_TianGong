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
public class SatelliteDataApiClient {

    private final ExternalApiClient apiClient;
    private final ApiProperties apiProperties;
    private WebClient webClient;

    public SatelliteDataApiClient(ExternalApiClient apiClient, ApiProperties apiProperties) {
        this.apiClient = apiClient;
        this.apiProperties = apiProperties;
    }

    @PostConstruct
    public void init() {
        this.webClient = apiClient.createWebClient("https://disc.gsfc.nasa.gov", 30);
    }

    public JsonNode getOCO2XCO2(double longitude, double latitude, String startDate, String endDate) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("lon", String.valueOf(longitude));
            params.put("lat", String.valueOf(latitude));
            params.put("start", startDate);
            params.put("end", endDate);
            params.put("product", "OCO2_L2_Lite_FP");
            return apiClient.get(webClient, "/api/oco2/xco2", params);
        } catch (Exception e) {
            log.warn("获取OCO-2 XCO2数据失败: {}", e.getMessage());
            return null;
        }
    }

    public JsonNode getGOSATXCO2(double longitude, double latitude, String startDate, String endDate) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("lon", String.valueOf(longitude));
            params.put("lat", String.valueOf(latitude));
            params.put("start", startDate);
            params.put("end", endDate);
            params.put("product", "GOSAT_TANSO");
            return apiClient.get(webClient, "/api/gosat/xco2", params);
        } catch (Exception e) {
            log.warn("获取GOSAT XCO2数据失败: {}", e.getMessage());
            return null;
        }
    }

    public JsonNode getMODISGPP(double longitude, double latitude, int year) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("lon", String.valueOf(longitude));
            params.put("lat", String.valueOf(latitude));
            params.put("year", String.valueOf(year));
            params.put("product", "MOD17A2H.061");
            return apiClient.get(webClient, "/api/modis/gpp", params);
        } catch (Exception e) {
            log.warn("获取MODIS GPP数据失败: {}", e.getMessage());
            return null;
        }
    }
}
