package com.ecocarbon.mrv.ai;

import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmissionPrediction {
    private List<Map<String, Object>> predictions;
    private String trend;
    private String confidence;
}
