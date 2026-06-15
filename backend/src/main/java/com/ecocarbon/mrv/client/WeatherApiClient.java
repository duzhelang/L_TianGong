package com.ecocarbon.mrv.client;

import com.ecocarbon.mrv.config.ApiProperties;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class WeatherApiClient {

    private final ExternalApiClient apiClient;
    private final ApiProperties apiProperties;
    private final WebClient webClient;

    public WeatherApiClient(ExternalApiClient apiClient, ApiProperties apiProperties) {
        this.apiClient = apiClient;
        this.apiProperties = apiProperties;
        this.webClient = apiClient.createWebClient(
                apiProperties.getWeather().getBaseUrl(),
                apiProperties.getWeather().getTimeoutSeconds()
        );
    }

    public JsonNode getCurrentWeather(double longitude, double latitude) {
        String location = String.format("%.2f,%.2f", longitude, latitude);
        Map<String, String> params = new HashMap<>();
        params.put("location", location);
        params.put("key", apiProperties.getWeather().getApiKey());
        return apiClient.get(webClient, "/v7/weather/now", params);
    }

    public JsonNode getWeatherForecast(double longitude, double latitude, int days) {
        String location = String.format("%.2f,%.2f", longitude, latitude);
        Map<String, String> params = new HashMap<>();
        params.put("location", location);
        params.put("key", apiProperties.getWeather().getApiKey());
        params.put("days", String.valueOf(days));
        return apiClient.get(webClient, "/v7/weather/3d", params);
    }

    public JsonNode getHistoricalWeather(double longitude, double latitude, LocalDate date) {
        String location = String.format("%.2f,%.2f", longitude, latitude);
        String dateStr = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Map<String, String> params = new HashMap<>();
        params.put("location", location);
        params.put("key", apiProperties.getWeather().getApiKey());
        params.put("date", dateStr);
        return apiClient.get(webClient, "/v7/historical/weather", params);
    }

    public JsonNode getAirQuality(double longitude, double latitude) {
        String location = String.format("%.2f,%.2f", longitude, latitude);
        Map<String, String> params = new HashMap<>();
        params.put("location", location);
        params.put("key", apiProperties.getWeather().getApiKey());
        return apiClient.get(webClient, "/airquality/v1/current/" + location, params);
    }

    public JsonNode getSolarRadiation(double longitude, double latitude) {
        String location = String.format("%.2f,%.2f", longitude, latitude);
        Map<String, String> params = new HashMap<>();
        params.put("location", location);
        params.put("key", apiProperties.getWeather().getApiKey());
        return apiClient.get(webClient, "/v7/solar/radiation", params);
    }
}