package com.ecocarbon.mrv.dto;

import java.time.LocalDateTime;

public record NPPInversionResponse(
        Double nppValue,
        String unit,
        Double par,
        Double fpar,
        Double epsilon,
        Double temperatureStress,
        Double waterStress,
        String model,
        LocalDateTime calculatedAt
) {
}
