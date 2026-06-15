package com.ecocarbon.mrv.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NPPInversionRequest(
        @NotBlank String areaId,
        @NotNull Integer year,
        Integer month,
        @NotNull Double par,
        @NotNull Double fpar,
        @NotNull Double epsilon,
        @NotNull Double temperature,
        @NotNull Double optimalTemperature,
        @NotNull Double soilMoisture,
        @NotNull Double optimalSoilMoisture,
        Double latitude,
        Double longitude
) {
}
