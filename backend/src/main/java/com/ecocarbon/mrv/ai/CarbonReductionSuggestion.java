package com.ecocarbon.mrv.ai;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CarbonReductionSuggestion {
    private String category;
    private String title;
    private String description;
    private Double potentialReduction;
    private String unit;
    private String difficulty;
    private String paybackPeriod;
}
