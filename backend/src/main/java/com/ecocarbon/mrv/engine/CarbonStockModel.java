package com.ecocarbon.mrv.engine;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class CarbonStockModel {

    public Map<String, Object> calculateSOCChange(double initialSOC, double annualCarbonInput,
                                                  double decompositionRate, double landUseFactor,
                                                  int years) {
        double finalSOC = initialSOC;
        for (int i = 0; i < years; i++) {
            double carbonLoss = finalSOC * decompositionRate;
            double carbonGain = annualCarbonInput * landUseFactor;
            finalSOC = finalSOC - carbonLoss + carbonGain;
        }
        double socChange = finalSOC - initialSOC;

        Map<String, Object> result = new HashMap<>();
        result.put("initialSOC", initialSOC);
        result.put("finalSOC", round(finalSOC));
        result.put("socChange", round(socChange));
        result.put("annualChange", round(socChange / years));
        result.put("unit", "tC");
        return result;
    }

    public Map<String, Object> calculateSOCChangeTier2(double clayContent, double sandContent,
                                                       double socInitial, double temperature,
                                                       double precipitation, double landUseClass,
                                                       double managementFactor, int years) {
        double[] cFractions = {0.0, 0.0, 0.0, 0.0, 0.0};
        cFractions[0] = socInitial * 0.025;
        cFractions[1] = socInitial * 0.075;
        cFractions[2] = socInitial * 0.10;
        cFractions[3] = socInitial * 0.40;
        cFractions[4] = socInitial * 0.40;

        double[] decayRates = {10.0, 0.3, 0.66, 0.02, 0.0};
        double[] humificationFactors = {0.0, 0.45, 0.35, 0.25, 0.0};

        double tempFactor = Math.exp(0.08 * (temperature - 25));
        double moistureFactor = 1.0 + 0.05 * (precipitation - 1000) / 1000;

        for (int year = 0; year < years; year++) {
            double[] newFractions = new double[5];
            for (int i = 0; i < 5; i++) {
                double loss = cFractions[i] * decayRates[i] * tempFactor * moistureFactor * 0.01;
                newFractions[i] = cFractions[i] - loss;
                if (i < 4) {
                    newFractions[i + 1] += loss * humificationFactors[i] * managementFactor;
                }
            }
            cFractions = newFractions;
        }

        double finalSOC = 0;
        for (double f : cFractions) {
            finalSOC += f;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("initialSOC", round(socInitial));
        result.put("finalSOC", round(finalSOC));
        result.put("socChange", round(finalSOC - socInitial));
        result.put("unit", "tC/ha");
        return result;
    }

    public Map<String, Object> calculateVegetationCarbon(double biomass, double carbonContent,
                                                         double area) {
        double carbonStock = biomass * carbonContent * area;

        Map<String, Object> result = new HashMap<>();
        result.put("biomass", biomass);
        result.put("carbonContent", carbonContent);
        result.put("area", area);
        result.put("carbonStock", round(carbonStock));
        result.put("co2Equivalent", round(carbonStock * 44.0 / 12.0));
        result.put("unit", "tC");
        return result;
    }

    public Map<String, Object> calculateForestCarbonStock(double dbh, double treeHeight,
                                                          double woodDensity, double crownRatio,
                                                          int treeCount, double area) {
        double abovegroundBiomass = 0.0509 * woodDensity * Math.pow(dbh, 2) * treeHeight;
        double belowgroundBiomass = abovegroundBiomass * 0.26;
        double totalBiomass = (abovegroundBiomass + belowgroundBiomass) * treeCount;
        double carbonStock = totalBiomass * 0.47;

        Map<String, Object> result = new HashMap<>();
        result.put("abovegroundBiomass", round(abovegroundBiomass));
        result.put("belowgroundBiomass", round(belowgroundBiomass));
        result.put("totalBiomass", round(totalBiomass));
        result.put("carbonStock", round(carbonStock));
        result.put("carbonDensity", round(carbonStock / area));
        result.put("co2Equivalent", round(carbonStock * 44.0 / 12.0));
        result.put("unit", "tC");
        return result;
    }

    public Map<String, Object> calculateLitterCarbon(double litterMass, double carbonContent,
                                                     double area, double decompositionRate) {
        double steadyStateLitter = litterMass / decompositionRate;
        double carbonStock = steadyStateLitter * carbonContent * area;

        Map<String, Object> result = new HashMap<>();
        result.put("litterMass", litterMass);
        result.put("steadyStateLitter", round(steadyStateLitter));
        result.put("carbonStock", round(carbonStock));
        result.put("co2Equivalent", round(carbonStock * 44.0 / 12.0));
        result.put("unit", "tC");
        return result;
    }

    public Map<String, Object> calculateTotalCarbonStock(Map<String, Object> socStock,
                                                         Map<String, Object> vegetationStock,
                                                         Map<String, Object> litterStock) {
        double totalSOC = ((Number) socStock.getOrDefault("finalSOC", 0.0)).doubleValue();
        double totalVegetation = ((Number) vegetationStock.getOrDefault("carbonStock", 0.0)).doubleValue();
        double totalLitter = ((Number) litterStock.getOrDefault("carbonStock", 0.0)).doubleValue();
        double totalCarbon = totalSOC + totalVegetation + totalLitter;

        Map<String, Object> result = new HashMap<>();
        result.put("soilOrganicCarbon", round(totalSOC));
        result.put("vegetationCarbon", round(totalVegetation));
        result.put("litterCarbon", round(totalLitter));
        result.put("totalCarbonStock", round(totalCarbon));
        result.put("totalCO2Equivalent", round(totalCarbon * 44.0 / 12.0));
        result.put("unit", "tC");
        return result;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
