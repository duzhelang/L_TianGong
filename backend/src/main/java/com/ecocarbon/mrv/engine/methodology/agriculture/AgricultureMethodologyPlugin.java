package com.ecocarbon.mrv.engine.methodology.agriculture;

import com.ecocarbon.mrv.engine.methodology.MethodologyPlugin;
import com.ecocarbon.mrv.engine.methodology.MethodologyResult;
import com.ecocarbon.mrv.engine.methodology.ParameterDefinition;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class AgricultureMethodologyPlugin implements MethodologyPlugin {

    @Override
    public String getCode() {
        return "AGRICULTURE_IPCC_2019";
    }

    @Override
    public String getName() {
        return "农业碳核算方法学（IPCC 2019）";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String getSceneType() {
        return "AGRICULTURE";
    }

    @Override
    public String getDescription() {
        return "基于IPCC 2019指南的农业碳核算方法学，涵盖稻田CH₄、施肥N₂O、畜牧排放等";
    }

    @Override
    public MethodologyResult calculate(Map<String, Object> parameters) {
        double paddyEmission = calculatePaddyEmission(parameters);
        double fertilizerEmission = calculateFertilizerEmission(parameters);
        double livestockEmission = calculateLivestockEmission(parameters);
        double totalEmission = paddyEmission + fertilizerEmission + livestockEmission;

        return MethodologyResult.builder()
                .totalEmission(totalEmission)
                .unit("tCO2e")
                .breakdown(Map.of(
                        "paddyCH4", paddyEmission,
                        "fertilizerN2O", fertilizerEmission,
                        "livestockCH4", livestockEmission
                ))
                .methodology(getName())
                .calculationProcess(String.format(
                        "稻田CH₄排放: %.2f tCO2e + 施肥N₂O排放: %.2f tCO2e + 畜牧排放: %.2f tCO2e = %.2f tCO2e",
                        paddyEmission, fertilizerEmission, livestockEmission, totalEmission
                ))
                .build();
    }

    @Override
    public List<ParameterDefinition> getParameterDefinitions() {
        return List.of(
                ParameterDefinition.builder().name("paddyArea").label("稻田面积").type("number").unit("公顷").required(true).description("水稻种植面积").build(),
                ParameterDefinition.builder().name("irrigationType").label("灌溉类型").type("select").required(true).description("持续灌溉/间歇灌溉/旱作").build(),
                ParameterDefinition.builder().name("nitrogenAmount").label("施氮量").type("number").unit("kg/ha").required(true).description("化肥施用量（纯氮）").build(),
                ParameterDefinition.builder().name("cattleCount").label("牛存栏数").type("number").unit("头").required(false).description("牛的存栏数量").defaultValue(0).build(),
                ParameterDefinition.builder().name("pigCount").label("猪存栏数").type("number").unit("头").required(false).description("猪的存栏数量").defaultValue(0).build()
        );
    }

    private double calculatePaddyEmission(Map<String, Object> params) {
        double area = getDoubleParam(params, "paddyArea", 0);
        double baseEf = 1.3;
        double irrigationFactor = "continuous".equals(params.get("irrigationType")) ? 1.0 : 0.7;
        return area * baseEf * irrigationFactor * 28 / 1000;
    }

    private double calculateFertilizerEmission(Map<String, Object> params) {
        double nitrogen = getDoubleParam(params, "nitrogenAmount", 0);
        double area = getDoubleParam(params, "paddyArea", 0);
        double ef = 0.01;
        return nitrogen * area * ef * 298 / 1000;
    }

    private double calculateLivestockEmission(Map<String, Object> params) {
        double cattle = getDoubleParam(params, "cattleCount", 0);
        double pig = getDoubleParam(params, "pigCount", 0);
        return (cattle * 53 * 28 + pig * 1.5 * 28) / 1000;
    }

    private double getDoubleParam(Map<String, Object> params, String key, double defaultValue) {
        Object value = params.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return defaultValue;
    }
}
