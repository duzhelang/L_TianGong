package com.ecocarbon.mrv.engine;

import com.ecocarbon.mrv.entity.ActivityData;
import com.ecocarbon.mrv.entity.EmissionFactor;
import com.ecocarbon.mrv.entity.MethodologyConfig;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScopeCalculator {
    private final ObjectMapper objectMapper;

    public double calculateByScope(ActivityData activityData, EmissionFactor emissionFactor, String scope, MethodologyConfig methodology) {
        switch (scope) {
            case "SCOPE_1":
                return calculateScope1(activityData, emissionFactor, methodology);
            case "SCOPE_2":
                return calculateScope2(activityData, emissionFactor, methodology);
            case "SCOPE_3":
                return calculateScope3(activityData, emissionFactor, methodology);
            default:
                throw new RuntimeException("不支持的核算范围: " + scope);
        }
    }

    private double calculateScope1(ActivityData activityData, EmissionFactor emissionFactor, MethodologyConfig methodology) {
        double activityValue = activityData.getValue();
        double factorValue = emissionFactor.getFactorValue();
        double baseEmission = activityValue * factorValue;

        Map<String, Double> parameters = parseParameters(methodology.getParametersConfig());
        if (parameters.containsKey("oxidationFactor")) {
            baseEmission *= parameters.get("oxidationFactor");
        }
        if (parameters.containsKey("globalWarmingPotential")) {
            baseEmission *= parameters.get("globalWarmingPotential");
        }

        return baseEmission;
    }

    private double calculateScope2(ActivityData activityData, EmissionFactor emissionFactor, MethodologyConfig methodology) {
        double activityValue = activityData.getValue();
        double factorValue = emissionFactor.getFactorValue();
        double baseEmission = activityValue * factorValue;

        Map<String, Double> parameters = parseParameters(methodology.getParametersConfig());
        if (parameters.containsKey("transmissionLossFactor")) {
            baseEmission *= (1 + parameters.get("transmissionLossFactor"));
        }

        return baseEmission;
    }

    private double calculateScope3(ActivityData activityData, EmissionFactor emissionFactor, MethodologyConfig methodology) {
        double activityValue = activityData.getValue();
        double factorValue = emissionFactor.getFactorValue();
        double baseEmission = activityValue * factorValue;

        Map<String, Double> parameters = parseParameters(methodology.getParametersConfig());
        if (parameters.containsKey("upstreamFactor")) {
            baseEmission *= parameters.get("upstreamFactor");
        }
        if (parameters.containsKey("downstreamFactor")) {
            baseEmission *= parameters.get("downstreamFactor");
        }

        return baseEmission;
    }

    private Map<String, Double> parseParameters(String parametersConfig) {
        try {
            if (parametersConfig == null || parametersConfig.isEmpty()) {
                return Map.of();
            }
            return objectMapper.readValue(parametersConfig, new TypeReference<Map<String, Double>>() {});
        } catch (Exception e) {
            log.error("解析方法学参数配置失败", e);
            return Map.of();
        }
    }
}