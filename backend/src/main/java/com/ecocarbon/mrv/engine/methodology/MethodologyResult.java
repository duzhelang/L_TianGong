package com.ecocarbon.mrv.engine.methodology;

import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MethodologyResult {
    private double totalEmission;
    private String unit;
    private Map<String, Double> breakdown;
    private String methodology;
    private String calculationProcess;
}
