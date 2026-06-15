package com.ecocarbon.mrv.engine.agriculture;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class PaddyMethaneCalculator {
    private static final double DEFAULT_EF = 1.3;
    private static final double GWP_CH4 = 28.0;

    private static final Map<String, Double> WATER_MANAGEMENT_FACTORS = Map.of(
            "continuous_flooding", 1.0,
            "intermittent_flooding", 0.68,
            "midseason_drainage", 0.52,
            "rainfed", 0.78,
            "dry_cultivation", 0.14
    );

    private static final Map<String, Double> ORGANIC_AMENDMENT_FACTORS = Map.of(
            "none", 1.0,
            "compost", 1.2,
            "straw_return", 1.4,
            "green_manure", 1.3,
            "farmyard_manure", 1.5
    );

    private static final Map<String, Double> SOIL_TYPE_FACTORS = Map.of(
            "clay", 1.15,
            "loam", 1.0,
            "sandy_loam", 0.88,
            "silt_loam", 1.05
    );

    public Map<String, Object> calculateTier1(double areaHa, String waterManagement,
                                               String organicAmendment, double cultivationDays) {
        double efDefault = DEFAULT_EF;
        double sfWater = WATER_MANAGEMENT_FACTORS.getOrDefault(waterManagement, 1.0);
        double sfOrganic = ORGANIC_AMENDMENT_FACTORS.getOrDefault(organicAmendment, 1.0);
        double seasonFactor = Math.min(cultivationDays / 120.0, 1.5);

        double ch4EmissionKg = efDefault * sfWater * sfOrganic * areaHa * seasonFactor;
        double co2e = ch4EmissionKg * GWP_CH4;

        Map<String, Object> result = new HashMap<>();
        result.put("methodology", "IPCC_2019_Tier1");
        result.put("areaHa", areaHa);
        result.put("emissionFactor", efDefault);
        result.put("sfWater", sfWater);
        result.put("sfOrganic", sfOrganic);
        result.put("seasonFactor", round(seasonFactor));
        result.put("ch4EmissionKg", round(ch4EmissionKg));
        result.put("co2eKg", round(co2e));
        result.put("co2eTon", round(co2e / 1000));
        result.put("unit", "kgCH4");
        return result;
    }

    public Map<String, Object> calculateTier2(double areaHa, String waterManagement,
                                               String organicAmendment, String soilType,
                                               double soilOrganicCarbon, double temperature,
                                               double precipitation, double cultivationDays,
                                               double riceYield) {
        double efDefault = DEFAULT_EF;
        double sfWater = WATER_MANAGEMENT_FACTORS.getOrDefault(waterManagement, 1.0);
        double sfOrganic = ORGANIC_AMENDMENT_FACTORS.getOrDefault(organicAmendment, 1.0);
        double sfSoil = SOIL_TYPE_FACTORS.getOrDefault(soilType, 1.0);

        double socFactor = 1.0 + 0.3 * Math.log10(Math.max(soilOrganicCarbon, 1.0));
        double tempFactor = Math.exp(0.04 * (temperature - 20));
        double precipFactor = 1.0 + 0.001 * (precipitation - 1000);
        double yieldFactor = Math.min(riceYield / 6.0, 1.2);

        double effectiveEf = efDefault * sfWater * sfOrganic * sfSoil * socFactor * tempFactor * precipFactor;
        double seasonFactor = Math.min(cultivationDays / 120.0, 1.5);

        double ch4EmissionKg = effectiveEf * areaHa * seasonFactor * yieldFactor;
        double co2e = ch4EmissionKg * GWP_CH4;

        Map<String, Object> result = new HashMap<>();
        result.put("methodology", "IPCC_2019_Tier2");
        result.put("areaHa", areaHa);
        result.put("baseEmissionFactor", efDefault);
        result.put("sfWater", sfWater);
        result.put("sfOrganic", sfOrganic);
        result.put("sfSoil", sfSoil);
        result.put("socFactor", round(socFactor));
        result.put("tempFactor", round(tempFactor));
        result.put("precipFactor", round(precipFactor));
        result.put("yieldFactor", round(yieldFactor));
        result.put("effectiveEmissionFactor", round(effectiveEf));
        result.put("ch4EmissionKg", round(ch4EmissionKg));
        result.put("co2eKg", round(co2e));
        result.put("co2eTon", round(co2e / 1000));
        result.put("unit", "kgCH4");
        return result;
    }

    public Map<String, Object> calculateWithCustomEF(double areaHa, double customEF,
                                                      String waterManagement, String organicAmendment) {
        double sfWater = WATER_MANAGEMENT_FACTORS.getOrDefault(waterManagement, 1.0);
        double sfOrganic = ORGANIC_AMENDMENT_FACTORS.getOrDefault(organicAmendment, 1.0);

        double ch4EmissionKg = customEF * sfWater * sfOrganic * areaHa;
        double co2e = ch4EmissionKg * GWP_CH4;

        Map<String, Object> result = new HashMap<>();
        result.put("methodology", "CUSTOM");
        result.put("areaHa", areaHa);
        result.put("customEmissionFactor", customEF);
        result.put("sfWater", sfWater);
        result.put("sfOrganic", sfOrganic);
        result.put("ch4EmissionKg", round(ch4EmissionKg));
        result.put("co2eKg", round(co2e));
        result.put("co2eTon", round(co2e / 1000));
        result.put("unit", "kgCH4");
        return result;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
