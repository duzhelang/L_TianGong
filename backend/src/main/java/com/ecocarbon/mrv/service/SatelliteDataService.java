package com.ecocarbon.mrv.service;

import com.ecocarbon.mrv.client.SatelliteDataApiClient;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SatelliteDataService {
    private final SatelliteDataApiClient satelliteDataApiClient;

    public Map<String, Object> getOCO2Data(double longitude, double latitude,
                                            String startDate, String endDate) {
        JsonNode data = satelliteDataApiClient.getOCO2XCO2(longitude, latitude, startDate, endDate);
        Map<String, Object> result = new HashMap<>();
        result.put("source", "OCO-2");
        result.put("longitude", longitude);
        result.put("latitude", latitude);
        result.put("startDate", startDate);
        result.put("endDate", endDate);
        if (data != null) {
            result.put("data", parseXCO2Data(data));
            result.put("status", "success");
        } else {
            result.put("data", new ArrayList<>());
            result.put("status", "no_data");
            result.put("message", "OCO-2数据暂不可用，请检查API配置或网络连接");
        }
        return result;
    }

    public Map<String, Object> getGOSATData(double longitude, double latitude,
                                             String startDate, String endDate) {
        JsonNode data = satelliteDataApiClient.getGOSATXCO2(longitude, latitude, startDate, endDate);
        Map<String, Object> result = new HashMap<>();
        result.put("source", "GOSAT");
        result.put("longitude", longitude);
        result.put("latitude", latitude);
        if (data != null) {
            result.put("data", parseXCO2Data(data));
            result.put("status", "success");
        } else {
            result.put("data", new ArrayList<>());
            result.put("status", "no_data");
            result.put("message", "GOSAT数据暂不可用，请检查API配置或网络连接");
        }
        return result;
    }

    public Map<String, Object> getGPPData(double longitude, double latitude, int year) {
        JsonNode data = satelliteDataApiClient.getMODISGPP(longitude, latitude, year);
        Map<String, Object> result = new HashMap<>();
        result.put("source", "MOD17A2H");
        result.put("longitude", longitude);
        result.put("latitude", latitude);
        result.put("year", year);
        if (data != null) {
            result.put("data", parseGPPData(data));
            result.put("status", "success");
        } else {
            result.put("data", new ArrayList<>());
            result.put("status", "no_data");
            result.put("message", "MODIS GPP数据暂不可用");
        }
        return result;
    }

    private List<Map<String, Object>> parseXCO2Data(JsonNode data) {
        List<Map<String, Object>> results = new ArrayList<>();
        if (data.isArray()) {
            for (JsonNode item : data) {
                Map<String, Object> point = new HashMap<>();
                if (item.has("xco2")) point.put("xco2", item.get("xco2").asDouble());
                if (item.has("date")) point.put("date", item.get("date").asText());
                if (item.has("longitude")) point.put("longitude", item.get("longitude").asDouble());
                if (item.has("latitude")) point.put("latitude", item.get("latitude").asDouble());
                if (item.has("quality")) point.put("quality", item.get("quality").asInt());
                results.add(point);
            }
        }
        return results;
    }

    private List<Map<String, Object>> parseGPPData(JsonNode data) {
        List<Map<String, Object>> results = new ArrayList<>();
        if (data.isArray()) {
            for (JsonNode item : data) {
                Map<String, Object> point = new HashMap<>();
                if (item.has("gpp")) point.put("gpp", item.get("gpp").asDouble());
                if (item.has("date")) point.put("date", item.get("date").asText());
                if (item.has("quality")) point.put("quality", item.get("quality").asInt());
                results.add(point);
            }
        }
        return results;
    }
}
