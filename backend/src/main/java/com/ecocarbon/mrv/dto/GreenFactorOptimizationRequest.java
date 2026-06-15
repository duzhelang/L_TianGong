package com.ecocarbon.mrv.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GreenFactorOptimizationRequest(
        @NotNull Long projectId,
        @NotBlank String region,
        @NotNull Double electricityKwh,
        String energyType,
        String timePeriod
) {
}