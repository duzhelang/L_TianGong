package com.ecocarbon.mrv.dto;

import jakarta.validation.constraints.NotBlank;

public record MethodologyConfigRequest(
        @NotBlank String methodologyCode,
        @NotBlank String methodologyName,
        String version,
        String description,
        String applicableScenes,
        String calculationFormula,
        String parametersConfig,
        String referenceStandard
) {
}