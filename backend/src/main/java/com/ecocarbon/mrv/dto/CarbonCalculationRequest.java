package com.ecocarbon.mrv.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CarbonCalculationRequest(
        @NotNull Long projectId,
        @NotNull Long activityDataId,
        @NotNull Long emissionFactorId,
        @NotBlank String scope
) {
}