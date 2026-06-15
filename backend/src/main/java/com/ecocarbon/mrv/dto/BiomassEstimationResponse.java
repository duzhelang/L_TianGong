package com.ecocarbon.mrv.dto;

import java.time.LocalDateTime;

public record BiomassEstimationResponse(
        Double biomass,
        String biomassUnit,
        Double carbonStock,
        String carbonUnit,
        Double co2Equivalent,
        String co2Unit,
        String vegetationType,
        String method,
        LocalDateTime calculatedAt
) {
}
