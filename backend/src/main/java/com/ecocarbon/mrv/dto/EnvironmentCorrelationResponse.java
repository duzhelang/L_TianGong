package com.ecocarbon.mrv.dto;

import java.util.List;
import java.util.Map;

public record EnvironmentCorrelationResponse(
        String areaId,
        List<Map<String, Object>> correlations,
        Map<String, Double> carbonProcesses,
        String overallRisk
) {
}
