package com.ecocarbon.mrv.dto;

import java.util.List;

public record GreenFactorOptimizationResponse(
        Double recommendedFactor,
        String unit,
        String source,
        List<String> optimizationSuggestions,
        Double potentialReduction,
        String confidenceLevel
) {
}