package com.ecocarbon.mrv.engine.methodology.forestry;

import com.ecocarbon.mrv.engine.methodology.MethodologyPlugin;
import com.ecocarbon.mrv.engine.methodology.MethodologyResult;
import com.ecocarbon.mrv.engine.methodology.ParameterDefinition;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ForestryMethodologyPlugin implements MethodologyPlugin {

    @Override
    public String getCode() {
        return "FORESTRY_CCER";
    }

    @Override
    public String getName() {
        return "林业碳汇方法学（CCER）";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String getSceneType() {
        return "FORESTRY";
    }

    @Override
    public String getDescription() {
        return "基于CCER标准的林业碳汇方法学，涵盖森林蓄积、造林再造林等";
    }

    @Override
    public MethodologyResult calculate(Map<String, Object> parameters) {
        double forestCarbonSink = calculateForestCarbonSink(parameters);
        double afforestationSink = calculateAfforestationSink(parameters);
        double totalSink = forestCarbonSink + afforestationSink;

        return MethodologyResult.builder()
                .totalEmission(-totalSink)
                .unit("tCO2e")
                .breakdown(Map.of(
                        "forestStock", -forestCarbonSink,
                        "afforestation", -afforestationSink
                ))
                .methodology(getName())
                .calculationProcess(String.format(
                        "森林蓄积碳汇: %.2f tCO2e + 造林碳汇: %.2f tCO2e = %.2f tCO2e",
                        forestCarbonSink, afforestationSink, totalSink
                ))
                .build();
    }

    @Override
    public List<ParameterDefinition> getParameterDefinitions() {
        return List.of(
                ParameterDefinition.builder().name("forestArea").label("森林面积").type("number").unit("公顷").required(true).description("森林覆盖面积").build(),
                ParameterDefinition.builder().name("stockVolume").label("蓄积量").type("number").unit("m³/ha").required(true).description("单位面积蓄积量").build(),
                ParameterDefinition.builder().name("afforestationArea").label("造林面积").type("number").unit("公顷").required(false).description("新造林面积").defaultValue(0).build(),
                ParameterDefinition.builder().name("treeSpecies").label("树种").type("select").required(true).description("主要树种类型").build()
        );
    }

    private double calculateForestCarbonSink(Map<String, Object> params) {
        double area = getDoubleParam(params, "forestArea", 0);
        double volume = getDoubleParam(params, "stockVolume", 0);
        double bef = 1.3;
        double rsr = 0.24;
        double cf = 0.5;
        return area * volume * bef * (1 + rsr) * cf * 3.67 / 1000;
    }

    private double calculateAfforestationSink(Map<String, Object> params) {
        double area = getDoubleParam(params, "afforestationArea", 0);
        double annualIncrement = 5.0;
        return area * annualIncrement * 0.5 * 3.67 / 1000;
    }

    private double getDoubleParam(Map<String, Object> params, String key, double defaultValue) {
        Object value = params.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return defaultValue;
    }
}
