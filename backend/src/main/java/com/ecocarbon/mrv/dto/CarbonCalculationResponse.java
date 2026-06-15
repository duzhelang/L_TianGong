package com.ecocarbon.mrv.dto;

import java.time.LocalDate;

public record CarbonCalculationResponse(
        Long id,
        Long projectId,
        String projectName,
        String scope,
        String gasType,
        Double emissionValue,
        String unit,
        String calculationMethod,
        LocalDate periodStart,
        LocalDate periodEnd,
        String activityCategory,
        String factorCode,
        String factorName
) {
}