package com.ecocarbon.mrv.engine.agriculture;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class SoilCarbonSequestration {
    private static final double C_TO_CO2 = 44.0 / 12.0;

    private static final Map<String, double[]> ROTH_C_FRACTIONS = Map.of(
            "decomposable_plant_material", new double[]{0.39, 10.0},
            "resistant_plant_material", new double[]{0.18, 0.3},
            "microbial_biomass", new double[]{0.03, 0.66},
            "humified_organic_matter", new double[]{0.12, 0.02},
            "inert_organic_matter", new double[]{0.28, 0.0}
    );

    private static final Map<String, Double> TILLAGE_FACTORS = Map.of(
            "conventional", 1.0,
            "reduced", 0.85,
            "no_till", 0.70,
            "minimum", 0.78
    );

    private static final Map<String, Double> CROP_RESIDUE_FACTORS = Map.of(
            "wheat", 0.42,
            "rice", 0.40,
            "corn", 0.45,
            "soybean", 0.42,
            "cotton", 0.38,
            "vegetable", 0.35,
            "fruit", 0.45
    );

    public Map<String, Object> calculateStrawReturnEffect(double strawYieldTonHa, double strawReturnRatio,
                                                            String cropType, String soilType,
                                                            double initialSOCTonHa, int years) {
        double carbonContent = CROP_RESIDUE_FACTORS.getOrDefault(cropType, 0.40);
        double carbonInput = strawYieldTonHa * strawReturnRatio * carbonContent;

        double decayRate = getDecayRate(soilType);
        double cumulativeCarbon = 0;
        double soc = initialSOCTonHa;

        for (int i = 0; i < years; i++) {
            double decomposed = soc * decayRate;
            double retained = carbonInput * 0.3;
            soc = soc - decomposed + retained;
            cumulativeCarbon += retained;
        }

        double socChange = soc - initialSOCTonHa;
        double co2Sequestered = socChange * C_TO_CO2;

        Map<String, Object> result = new HashMap<>();
        result.put("practice", "straw_return");
        result.put("strawYieldTonHa", strawYieldTonHa);
        result.put("strawReturnRatio", strawReturnRatio);
        result.put("carbonInputPerYear", round(carbonInput));
        result.put("initialSOC", initialSOCTonHa);
        result.put("finalSOC", round(soc));
        result.put("socChange", round(socChange));
        result.put("annualSequestration", round(socChange / years));
        result.put("co2Sequestered", round(co2Sequestered));
        result.put("years", years);
        result.put("unit", "tC/ha");
        return result;
    }

    public Map<String, Object> calculateNoTillEffect(double conventionalSOC, String previousTillage,
                                                       String newTillage, double yearsUnderNewPractice) {
        double oldFactor = TILLAGE_FACTORS.getOrDefault(previousTillage, 1.0);
        double newFactor = TILLAGE_FACTORS.getOrDefault(newTillage, 1.0);
        double tillageEffect = oldFactor - newFactor;

        double equilibriumSOC = conventionalSOC / oldFactor;
        double targetSOC = equilibriumSOC * newFactor;
        double socChange = targetSOC - conventionalSOC;

        double yearsToEquilibrium = 20.0;
        double transitionFactor = 1 - Math.exp(-yearsUnderNewPractice / yearsToEquilibrium);
        double actualSOCChange = socChange * transitionFactor;
        double finalSOC = conventionalSOC + actualSOCChange;
        double co2Sequestered = actualSOCChange * C_TO_CO2;

        Map<String, Object> result = new HashMap<>();
        result.put("practice", "no_till");
        result.put("previousTillage", previousTillage);
        result.put("newTillage", newTillage);
        result.put("conventionalSOC", conventionalSOC);
        result.put("finalSOC", round(finalSOC));
        result.put("socChange", round(actualSOCChange));
        result.put("annualSequestration", round(actualSOCChange / yearsUnderNewPractice));
        result.put("co2Sequestered", round(co2Sequestered));
        result.put("transitionFactor", round(transitionFactor));
        result.put("years", yearsUnderNewPractice);
        result.put("unit", "tC/ha");
        return result;
    }

    public Map<String, Object> calculateCoverCropEffect(double coverCropBiomassTonHa,
                                                          double coverCropDurationMonths,
                                                          String soilType, double initialSOCTonHa,
                                                          int years) {
        double carbonContent = 0.42;
        double carbonInput = coverCropBiomassTonHa * carbonContent;
        double durationFactor = coverCropDurationMonths / 12.0;
        double effectiveInput = carbonInput * durationFactor;

        double decayRate = getDecayRate(soilType);
        double soc = initialSOCTonHa;

        for (int i = 0; i < years; i++) {
            double decomposed = soc * decayRate * 0.95;
            double retained = effectiveInput * 0.25;
            soc = soc - decomposed + retained;
        }

        double socChange = soc - initialSOCTonHa;
        double co2Sequestered = socChange * C_TO_CO2;

        Map<String, Object> result = new HashMap<>();
        result.put("practice", "cover_crop");
        result.put("coverCropBiomass", coverCropBiomassTonHa);
        result.put("carbonInputPerYear", round(effectiveInput));
        result.put("initialSOC", initialSOCTonHa);
        result.put("finalSOC", round(soc));
        result.put("socChange", round(socChange));
        result.put("annualSequestration", round(socChange / years));
        result.put("co2Sequestered", round(co2Sequestered));
        result.put("years", years);
        result.put("unit", "tC/ha");
        return result;
    }

    public Map<String, Object> calculateOrganicFarmingEffect(double organicInputTonHa,
                                                               String organicType,
                                                               double yearsUnderOrganic,
                                                               double initialSOCTonHa) {
        double carbonContent = getOrganicCarbonContent(organicType);
        double carbonInput = organicInputTonHa * carbonContent;
        double decompositionRate = 0.3;
        double humificationRate = 0.25;

        double equilibriumIncrease = carbonInput * humificationRate / decompositionRate;
        double yearsToEquilibrium = 15.0;
        double transitionFactor = 1 - Math.exp(-yearsUnderOrganic / yearsToEquilibrium);
        double socChange = equilibriumIncrease * transitionFactor;
        double finalSOC = initialSOCTonHa + socChange;
        double co2Sequestered = socChange * C_TO_CO2;

        Map<String, Object> result = new HashMap<>();
        result.put("practice", "organic_farming");
        result.put("organicInputTonHa", organicInputTonHa);
        result.put("organicType", organicType);
        result.put("carbonInputPerYear", round(carbonInput));
        result.put("initialSOC", initialSOCTonHa);
        result.put("finalSOC", round(finalSOC));
        result.put("socChange", round(socChange));
        result.put("annualSequestration", round(socChange / yearsUnderOrganic));
        result.put("co2Sequestered", round(co2Sequestered));
        result.put("years", yearsUnderOrganic);
        result.put("unit", "tC/ha");
        return result;
    }

    public Map<String, Object> calculateTotalSequestration(Map<String, Map<String, Object>> practices) {
        double totalSOCChange = 0;
        double totalCO2 = 0;

        for (Map<String, Object> practice : practices.values()) {
            totalSOCChange += (Double) practice.getOrDefault("socChange", 0.0);
            totalCO2 += (Double) practice.getOrDefault("co2Sequestered", 0.0);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("practices", practices.keySet());
        result.put("totalSOCChange", round(totalSOCChange));
        result.put("totalCO2Sequestered", round(totalCO2));
        result.put("unit", "tC/ha");
        return result;
    }

    private double getDecayRate(String soilType) {
        return switch (soilType) {
            case "clay" -> 0.02;
            case "loam" -> 0.03;
            case "sandy" -> 0.04;
            case "silt" -> 0.025;
            default -> 0.03;
        };
    }

    private double getOrganicCarbonContent(String type) {
        return switch (type) {
            case "compost" -> 0.20;
            case "manure" -> 0.25;
            case "green_manure" -> 0.40;
            case "crop_residue" -> 0.42;
            case "biochar" -> 0.70;
            default -> 0.30;
        };
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
