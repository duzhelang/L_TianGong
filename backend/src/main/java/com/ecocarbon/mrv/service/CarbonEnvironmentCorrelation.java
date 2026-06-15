package com.ecocarbon.mrv.service;

import com.ecocarbon.mrv.dto.EnvironmentCorrelationResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class CarbonEnvironmentCorrelation {

    public EnvironmentCorrelationResponse analyzeCorrelation(String areaId, Map<String, Double> environmentData) {
        List<Map<String, Object>> correlations = new ArrayList<>();
        Map<String, Double> carbonProcesses = new HashMap<>();

        analyzeSoilRespiration(environmentData, correlations, carbonProcesses);
        analyzePaddyMethane(environmentData, correlations, carbonProcesses);
        analyzeN2OEmission(environmentData, correlations, carbonProcesses);
        analyzeMethaneOxidation(environmentData, correlations, carbonProcesses);

        return new EnvironmentCorrelationResponse(
                areaId,
                correlations,
                carbonProcesses,
                calculateOverallRisk(correlations)
        );
    }

    private void analyzeSoilRespiration(Map<String, Double> data, List<Map<String, Object>> correlations, Map<String, Double> carbonProcesses) {
        Double temperature = data.get("soilTemperature");
        Double moisture = data.get("soilMoisture");

        if (temperature != null && moisture != null) {
            double respirationRate = calculateSoilRespiration(temperature, moisture);
            carbonProcesses.put("soilRespiration", respirationRate);

            Map<String, Object> correlation = new HashMap<>();
            correlation.put("process", "土壤呼吸");
            correlation.put("factors", Map.of("土壤温度", temperature, "土壤湿度", moisture));
            correlation.put("rate", respirationRate);
            correlation.put("unit", "gCO2/m2/day");
            correlation.put("riskLevel", assessRespirationRisk(respirationRate));
            correlations.add(correlation);
        }
    }

    private void analyzePaddyMethane(Map<String, Double> data, List<Map<String, Object>> correlations, Map<String, Double> carbonProcesses) {
        Double redoxPotential = data.get("soilConductivity");
        Double moisture = data.get("soilMoisture");

        if (redoxPotential != null && moisture != null) {
            double methaneRate = calculatePaddyMethane(redoxPotential, moisture);
            carbonProcesses.put("paddyMethane", methaneRate);

            Map<String, Object> correlation = new HashMap<>();
            correlation.put("process", "稻田CH4产生");
            correlation.put("factors", Map.of("氧化还原电位", redoxPotential, "土壤湿度", moisture));
            correlation.put("rate", methaneRate);
            correlation.put("unit", "mgCH4/m2/day");
            correlation.put("riskLevel", assessMethaneRisk(methaneRate));
            correlations.add(correlation);
        }
    }

    private void analyzeN2OEmission(Map<String, Double> data, List<Map<String, Object>> correlations, Map<String, Double> carbonProcesses) {
        Double ammoniaConcentration = data.get("soilConductivity");
        Double moisture = data.get("soilMoisture");

        if (ammoniaConcentration != null && moisture != null) {
            double n2oRate = calculateN2OEmission(ammoniaConcentration, moisture);
            carbonProcesses.put("n2oEmission", n2oRate);

            Map<String, Object> correlation = new HashMap<>();
            correlation.put("process", "N2O间接排放");
            correlation.put("factors", Map.of("氨气浓度", ammoniaConcentration, "土壤湿度", moisture));
            correlation.put("rate", n2oRate);
            correlation.put("unit", "μgN2O/m2/hour");
            correlation.put("riskLevel", assessN2ORisk(n2oRate));
            correlations.add(correlation);
        }
    }

    private void analyzeMethaneOxidation(Map<String, Double> data, List<Map<String, Object>> correlations, Map<String, Double> carbonProcesses) {
        Double dissolvedOxygen = data.get("lightIntensity");

        if (dissolvedOxygen != null) {
            double oxidationRate = calculateMethaneOxidation(dissolvedOxygen);
            carbonProcesses.put("methaneOxidation", oxidationRate);

            Map<String, Object> correlation = new HashMap<>();
            correlation.put("process", "甲烷氧化");
            correlation.put("factors", Map.of("溶解氧", dissolvedOxygen));
            correlation.put("rate", oxidationRate);
            correlation.put("unit", "μgCH4/g土壤/天");
            correlation.put("riskLevel", assessOxidationRisk(oxidationRate));
            correlations.add(correlation);
        }
    }

    private double calculateSoilRespiration(double temperature, double moisture) {
        double baseRate = 2.5;
        double tempFactor = Math.exp(0.07 * (temperature - 20));
        double moistureFactor = Math.min(1.0, moisture / 60.0);
        return baseRate * tempFactor * moistureFactor;
    }

    private double calculatePaddyMethane(double redoxPotential, double moisture) {
        double baseRate = 5.0;
        double redoxFactor = Math.max(0, (200 - redoxPotential) / 200.0);
        double moistureFactor = Math.min(1.0, moisture / 80.0);
        return baseRate * redoxFactor * moistureFactor;
    }

    private double calculateN2OEmission(double ammoniaConcentration, double moisture) {
        double baseRate = 10.0;
        double concentrationFactor = Math.min(1.0, ammoniaConcentration / 50.0);
        double moistureFactor = Math.min(1.0, moisture / 70.0);
        return baseRate * concentrationFactor * moistureFactor;
    }

    private double calculateMethaneOxidation(double dissolvedOxygen) {
        double baseRate = 0.5;
        double oxygenFactor = Math.min(1.0, dissolvedOxygen / 8.0);
        return baseRate * oxygenFactor;
    }

    private String assessRespirationRisk(double rate) {
        if (rate > 5.0) return "HIGH";
        if (rate > 3.0) return "MEDIUM";
        return "LOW";
    }

    private String assessMethaneRisk(double rate) {
        if (rate > 10.0) return "HIGH";
        if (rate > 5.0) return "MEDIUM";
        return "LOW";
    }

    private String assessN2ORisk(double rate) {
        if (rate > 20.0) return "HIGH";
        if (rate > 10.0) return "MEDIUM";
        return "LOW";
    }

    private String assessOxidationRisk(double rate) {
        if (rate < 0.2) return "HIGH";
        if (rate < 0.5) return "MEDIUM";
        return "LOW";
    }

    private String calculateOverallRisk(List<Map<String, Object>> correlations) {
        long highRiskCount = correlations.stream()
                .filter(c -> "HIGH".equals(c.get("riskLevel")))
                .count();
        if (highRiskCount >= 2) return "HIGH";
        if (highRiskCount >= 1) return "MEDIUM";
        return "LOW";
    }
}
