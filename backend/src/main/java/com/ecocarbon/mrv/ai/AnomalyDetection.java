package com.ecocarbon.mrv.ai;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnomalyDetection {
    private String timestamp;
    private String metric;
    private Double actualValue;
    private Double expectedValue;
    private Double deviation;
    private String severity;
    private String description;
}
