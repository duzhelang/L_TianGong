package com.ecocarbon.mrv.engine.methodology;

import java.util.List;
import java.util.Map;

public interface MethodologyPlugin {
    String getCode();
    String getName();
    String getVersion();
    String getSceneType();
    String getDescription();
    MethodologyResult calculate(Map<String, Object> parameters);
    List<ParameterDefinition> getParameterDefinitions();
}
