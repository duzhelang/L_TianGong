package com.ecocarbon.mrv.engine.methodology;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ParameterDefinition {
    private String name;
    private String label;
    private String type;
    private String unit;
    private boolean required;
    private String description;
    private Object defaultValue;
}
