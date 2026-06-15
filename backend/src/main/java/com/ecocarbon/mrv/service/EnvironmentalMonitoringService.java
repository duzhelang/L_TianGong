package com.ecocarbon.mrv.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class EnvironmentalMonitoringService {
    private final Map<String, Map<String, Double>> environmentData = new HashMap<>();

    public Map<String, Double> getEnvironmentParameters(String areaId) {
        return environmentData.getOrDefault(areaId, createDefaultEnvironmentData());
    }

    public List<Map<String, Object>> getEnvironmentAlerts(String areaId) {
        List<Map<String, Object>> alerts = new ArrayList<>();
        Map<String, Double> data = environmentData.getOrDefault(areaId, createDefaultEnvironmentData());

        checkTemperatureAlert(data, alerts);
        checkHumidityAlert(data, alerts);
        checkSoilMoistureAlert(data, alerts);
        checkSoilPHAlert(data, alerts);

        return alerts;
    }

    private Map<String, Double> createDefaultEnvironmentData() {
        Map<String, Double> data = new HashMap<>();
        data.put("temperature", 25.0);
        data.put("humidity", 65.0);
        data.put("soilMoisture", 35.0);
        data.put("soilTemperature", 22.0);
        data.put("soilPH", 6.8);
        data.put("soilConductivity", 1.2);
        data.put("lightIntensity", 800.0);
        data.put("windSpeed", 2.5);
        data.put("precipitation", 0.0);
        return data;
    }

    private void checkTemperatureAlert(Map<String, Double> data, List<Map<String, Object>> alerts) {
        Double temp = data.get("temperature");
        if (temp != null) {
            if (temp > 35.0) {
                alerts.add(createAlert("HIGH_TEMPERATURE", "HIGH", "环境温度过高: " + temp + "°C"));
            } else if (temp < 5.0) {
                alerts.add(createAlert("LOW_TEMPERATURE", "MEDIUM", "环境温度过低: " + temp + "°C"));
            }
        }
    }

    private void checkHumidityAlert(Map<String, Double> data, List<Map<String, Object>> alerts) {
        Double humidity = data.get("humidity");
        if (humidity != null) {
            if (humidity > 90.0) {
                alerts.add(createAlert("HIGH_HUMIDITY", "MEDIUM", "湿度过高: " + humidity + "%"));
            } else if (humidity < 30.0) {
                alerts.add(createAlert("LOW_HUMIDITY", "MEDIUM", "湿度过低: " + humidity + "%"));
            }
        }
    }

    private void checkSoilMoistureAlert(Map<String, Double> data, List<Map<String, Object>> alerts) {
        Double moisture = data.get("soilMoisture");
        if (moisture != null) {
            if (moisture > 80.0) {
                alerts.add(createAlert("HIGH_SOIL_MOISTURE", "HIGH", "土壤湿度过高: " + moisture + "%"));
            } else if (moisture < 20.0) {
                alerts.add(createAlert("LOW_SOIL_MOISTURE", "HIGH", "土壤湿度过低: " + moisture + "%"));
            }
        }
    }

    private void checkSoilPHAlert(Map<String, Double> data, List<Map<String, Object>> alerts) {
        Double ph = data.get("soilPH");
        if (ph != null) {
            if (ph > 8.5) {
                alerts.add(createAlert("HIGH_SOIL_PH", "MEDIUM", "土壤pH值过高: " + ph));
            } else if (ph < 5.5) {
                alerts.add(createAlert("LOW_SOIL_PH", "MEDIUM", "土壤pH值过低: " + ph));
            }
        }
    }

    private Map<String, Object> createAlert(String type, String severity, String message) {
        Map<String, Object> alert = new HashMap<>();
        alert.put("type", type);
        alert.put("severity", severity);
        alert.put("message", message);
        alert.put("timestamp", LocalDateTime.now());
        return alert;
    }

    public void updateEnvironmentData(String areaId, Map<String, Double> data) {
        environmentData.put(areaId, data);
    }
}
