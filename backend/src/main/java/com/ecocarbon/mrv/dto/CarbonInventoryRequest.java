package com.ecocarbon.mrv.dto;

import jakarta.validation.constraints.NotNull;

public record CarbonInventoryRequest(
        @NotNull Double fertilizerNitrogenKg,
        @NotNull Double paddyAreaHa,
        @NotNull Double electricityKwh,
        @NotNull Double soilCarbonChangeTonC
) {
}
