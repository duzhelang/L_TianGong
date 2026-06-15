package com.ecocarbon.mrv.service;

import com.ecocarbon.mrv.dto.CarbonInventoryRequest;
import com.ecocarbon.mrv.engine.CarbonStockModel;
import com.ecocarbon.mrv.engine.MRVReportGenerator;
import com.ecocarbon.mrv.engine.UncertaintyAnalyzer;
import com.ecocarbon.mrv.engine.agriculture.FarmEnergyCalculator;
import com.ecocarbon.mrv.engine.agriculture.FertilizerN2OCalculator;
import com.ecocarbon.mrv.engine.agriculture.LivestockMethaneCalculator;
import com.ecocarbon.mrv.engine.agriculture.PaddyMethaneCalculator;
import com.ecocarbon.mrv.engine.agriculture.SoilCarbonSequestration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarbonEngineService {
    private final PaddyMethaneCalculator paddyMethaneCalculator;
    private final FertilizerN2OCalculator fertilizerN2OCalculator;
    private final LivestockMethaneCalculator livestockMethaneCalculator;
    private final SoilCarbonSequestration soilCarbonSequestration;
    private final FarmEnergyCalculator farmEnergyCalculator;
    private final CarbonStockModel carbonStockModel;
    private final UncertaintyAnalyzer uncertaintyAnalyzer;
    private final MRVReportGenerator mrvReportGenerator;

    public Map<String, Object> calculateInventory(CarbonInventoryRequest request) {
        double n2oEmission = request.fertilizerNitrogenKg() * 0.01 * 44.0 / 28.0 * 273.0;
        double ch4Emission = request.paddyAreaHa() * 1.3 * 28.0;
        double electricityEmission = request.electricityKwh() * 0.5703;
        double sinkRemoval = request.soilCarbonChangeTonC() * 44.0 / 12.0;
        double netEmission = n2oEmission + ch4Emission + electricityEmission - sinkRemoval;

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("scope1", round(n2oEmission + ch4Emission));
        result.put("scope2", round(electricityEmission));
        result.put("carbonSinkRemoval", round(sinkRemoval));
        result.put("netEmissionCo2e", round(netEmission));
        result.put("unit", "kgCO2e");
        return result;
    }

    public Map<String, Object> calculateAgricultureEmission(double paddyAreaHa,
                                                              String waterManagement,
                                                              String organicAmendment,
                                                              double cultivationDays,
                                                              double syntheticNKg,
                                                              double organicNKg,
                                                              double electricityKwh,
                                                              String electricitySource) {
        Map<String, Object> ch4Result = paddyMethaneCalculator.calculateTier1(
                paddyAreaHa, waterManagement, organicAmendment, cultivationDays);

        Map<String, Object> n2oResult = fertilizerN2OCalculator.calculateTotalEmission(
                syntheticNKg, organicNKg, 0);

        Map<String, Object> energyResult = farmEnergyCalculator.calculateIrrigationEmission(
                electricityKwh, electricitySource, paddyAreaHa);

        double scope1 = (Double) ch4Result.get("co2eKg") + (Double) n2oResult.get("totalCo2eKg");
        double scope2 = (Double) energyResult.get("emissionKgCO2e");

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("methaneEmission", ch4Result);
        result.put("nitrousOxideEmission", n2oResult);
        result.put("energyEmission", energyResult);
        result.put("scope1", round(scope1));
        result.put("scope2", round(scope2));
        result.put("totalEmission", round(scope1 + scope2));
        result.put("unit", "kgCO2e");
        return result;
    }

    public Map<String, Object> calculateLivestockEmission(Map<String, Integer> livestockInventory,
                                                            Map<String, String> manureSystems,
                                                            Map<String, Double> weightFactors) {
        return livestockMethaneCalculator.calculateTotalLivestockEmission(
                livestockInventory, manureSystems, weightFactors);
    }

    public Map<String, Object> calculateCarbonSequestration(double strawYieldTonHa,
                                                              double strawReturnRatio,
                                                              String cropType,
                                                              String soilType,
                                                              double initialSOCTonHa,
                                                              int years) {
        return soilCarbonSequestration.calculateStrawReturnEffect(
                strawYieldTonHa, strawReturnRatio, cropType, soilType, initialSOCTonHa, years);
    }

    public Map<String, Object> calculateFullCarbonAccounting(String projectName,
                                                               LocalDate periodStart,
                                                               LocalDate periodEnd,
                                                               double paddyAreaHa,
                                                               String waterManagement,
                                                               String organicAmendment,
                                                               double cultivationDays,
                                                               double syntheticNKg,
                                                               double organicNKg,
                                                               double electricityKwh,
                                                               String electricitySource,
                                                               Map<String, Integer> livestockInventory,
                                                               Map<String, String> manureSystems,
                                                               Map<String, Double> livestockWeightFactors,
                                                               double strawYieldTonHa,
                                                               double strawReturnRatio,
                                                               String cropType,
                                                               String soilType,
                                                               double initialSOCTonHa) {
        Map<String, Object> agriEmission = calculateAgricultureEmission(
                paddyAreaHa, waterManagement, organicAmendment, cultivationDays,
                syntheticNKg, organicNKg, electricityKwh, electricitySource);

        Map<String, Object> livestockEmission = new HashMap<>();
        double livestockCo2e = 0;
        if (livestockInventory != null && !livestockInventory.isEmpty()) {
            livestockEmission = calculateLivestockEmission(livestockInventory, manureSystems, livestockWeightFactors);
            livestockCo2e = (Double) livestockEmission.getOrDefault("totalCo2eKg", 0.0);
        }

        Map<String, Object> sequestration = calculateCarbonSequestration(
                strawYieldTonHa, strawReturnRatio, cropType, soilType, initialSOCTonHa, 1);

        double scope1 = (Double) agriEmission.get("scope1") + livestockCo2e;
        double scope2 = (Double) agriEmission.get("scope2");
        double sinkRemoval = (Double) sequestration.getOrDefault("co2Sequestered", 0.0);
        double netEmission = scope1 + scope2 - sinkRemoval;

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("projectName", projectName);
        result.put("periodStart", periodStart.toString());
        result.put("periodEnd", periodEnd.toString());
        result.put("agricultureEmission", agriEmission);
        result.put("livestockEmission", livestockEmission);
        result.put("carbonSequestration", sequestration);
        result.put("scope1", round(scope1));
        result.put("scope2", round(scope2));
        result.put("carbonSinkRemoval", round(sinkRemoval));
        result.put("netEmissionCo2e", round(netEmission));
        result.put("unit", "kgCO2e");
        return result;
    }

    public Map<String, Object> generateUncertaintyAnalysis(double emissionValue,
                                                             Map<String, Double> uncertaintyContributions) {
        return uncertaintyAnalyzer.generateUncertaintyReport(emissionValue, uncertaintyContributions);
    }

    public Map<String, Object> generateMRVReport(String projectName, String projectCode,
                                                   LocalDate periodStart, LocalDate periodEnd,
                                                   Map<String, Double> scopeEmissions,
                                                   Map<String, Double> carbonSink) {
        Map<String, Object> uncertainty = uncertaintyAnalyzer.generateUncertaintyReport(
                scopeEmissions.values().stream().mapToDouble(Double::doubleValue).sum(),
                Map.of("activity_data", 0.1, "emission_factor", 0.15)
        );

        return mrvReportGenerator.generateCCERReport(
                projectName, projectCode, periodStart, periodEnd,
                scopeEmissions, carbonSink, uncertainty);
    }

    public Map<String, Object> calculateSOCChange(double initialSOC, double annualCarbonInput,
                                                    double decompositionRate, double landUseFactor,
                                                    int years) {
        Map<String, Object> result = carbonStockModel.calculateSOCChange(initialSOC, annualCarbonInput,
                decompositionRate, landUseFactor, years);
        return result;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
