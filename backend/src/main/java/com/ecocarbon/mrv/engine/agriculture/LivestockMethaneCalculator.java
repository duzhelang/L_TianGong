package com.ecocarbon.mrv.engine.agriculture;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class LivestockMethaneCalculator {
    private static final double GWP_CH4 = 28.0;

    private static final Map<String, Double> ENTERIC_EF = new HashMap<>() {{
        put("dairy_cattle", 117.0);
        put("beef_cattle", 53.0);
        put("sheep", 8.0);
        put("goats", 5.0);
        put("pigs", 1.5);
        put("horses", 18.0);
        put("mules", 10.0);
        put("buffalo", 55.0);
        put("poultry", 0.0);
        put("layers", 0.0);
        put("broilers", 0.0);
    }};

    private static final Map<String, Double> MANURE_CH4_EF = new HashMap<>() {{
        put("dairy_cattle", 11.0);
        put("beef_cattle", 1.5);
        put("sheep", 0.15);
        put("goats", 0.14);
        put("pigs", 3.0);
        put("horses", 1.64);
        put("mules", 0.9);
        put("buffalo", 2.0);
        put("poultry", 0.12);
        put("layers", 0.12);
        put("broilers", 0.12);
    }};

    private static final Map<String, Map<String, Double>> MANURE_SYSTEM_FACTORS = Map.of(
            "liquid_slurry", Map.of("dairy_cattle", 1.0, "beef_cattle", 1.0, "pigs", 1.0),
            "solid_storage", Map.of("dairy_cattle", 0.24, "beef_cattle", 0.24, "pigs", 0.24),
            "pasture_range", Map.of("dairy_cattle", 0.04, "beef_cattle", 0.04, "sheep", 0.04),
            "dry_lot", Map.of("dairy_cattle", 0.07, "beef_cattle", 0.07),
            "anaerobic_digestion", Map.of("dairy_cattle", 0.0, "beef_cattle", 0.0, "pigs", 0.0),
            "poultry_manure", Map.of("poultry", 0.14, "layers", 0.14, "broilers", 0.14)
    );

    public Map<String, Object> calculateEntericFermentation(String animalType, int headCount,
                                                              double weightFactor) {
        double ef = ENTERIC_EF.getOrDefault(animalType, 0.0);
        double ch4EmissionKg = ef * headCount * weightFactor / 1000.0;
        double co2e = ch4EmissionKg * GWP_CH4;

        Map<String, Object> result = new HashMap<>();
        result.put("animalType", animalType);
        result.put("headCount", headCount);
        result.put("emissionFactor", ef);
        result.put("weightFactor", weightFactor);
        result.put("ch4EmissionKg", round(ch4EmissionKg));
        result.put("co2eKg", round(co2e));
        result.put("co2eTon", round(co2e / 1000));
        result.put("unit", "kgCH4");
        return result;
    }

    public Map<String, Object> calculateManureManagement(String animalType, int headCount,
                                                           String manureSystem, double weightFactor) {
        double baseEf = MANURE_CH4_EF.getOrDefault(animalType, 0.0);
        Map<String, Double> systemFactors = MANURE_SYSTEM_FACTORS.getOrDefault(manureSystem, Map.of());
        double systemFactor = systemFactors.getOrDefault(animalType, 0.1);

        double ch4EmissionKg = baseEf * headCount * systemFactor * weightFactor / 1000.0;
        double co2e = ch4EmissionKg * GWP_CH4;

        Map<String, Object> result = new HashMap<>();
        result.put("animalType", animalType);
        result.put("headCount", headCount);
        result.put("manureSystem", manureSystem);
        result.put("baseEmissionFactor", baseEf);
        result.put("systemFactor", systemFactor);
        result.put("weightFactor", weightFactor);
        result.put("ch4EmissionKg", round(ch4EmissionKg));
        result.put("co2eKg", round(co2e));
        result.put("co2eTon", round(co2e / 1000));
        result.put("unit", "kgCH4");
        return result;
    }

    public Map<String, Object> calculateTotalLivestockEmission(Map<String, Integer> livestockInventory,
                                                                 Map<String, String> manureSystems,
                                                                 Map<String, Double> weightFactors) {
        double totalEntericCh4 = 0;
        double totalManureCh4 = 0;
        Map<String, Map<String, Object>> detailedResults = new HashMap<>();

        for (Map.Entry<String, Integer> entry : livestockInventory.entrySet()) {
            String animalType = entry.getKey();
            int headCount = entry.getValue();
            double weightFactor = weightFactors.getOrDefault(animalType, 1.0);
            String manureSystem = manureSystems.getOrDefault(animalType, "solid_storage");

            Map<String, Object> enteric = calculateEntericFermentation(animalType, headCount, weightFactor);
            Map<String, Object> manure = calculateManureManagement(animalType, headCount, manureSystem, weightFactor);

            totalEntericCh4 += (Double) enteric.get("ch4EmissionKg");
            totalManureCh4 += (Double) manure.get("ch4EmissionKg");

            Map<String, Object> animalResult = new HashMap<>();
            animalResult.put("enteric", enteric);
            animalResult.put("manure", manure);
            animalResult.put("totalCh4", round((Double) enteric.get("ch4EmissionKg") + (Double) manure.get("ch4EmissionKg")));
            detailedResults.put(animalType, animalResult);
        }

        double totalCh4 = totalEntericCh4 + totalManureCh4;
        double totalCo2e = totalCh4 * GWP_CH4;

        Map<String, Object> result = new HashMap<>();
        result.put("livestockInventory", livestockInventory);
        result.put("totalEntericCh4", round(totalEntericCh4));
        result.put("totalManureCh4", round(totalManureCh4));
        result.put("totalCh4", round(totalCh4));
        result.put("totalCo2eKg", round(totalCo2e));
        result.put("totalCo2eTon", round(totalCo2e / 1000));
        result.put("detailedResults", detailedResults);
        result.put("unit", "kgCH4");
        return result;
    }

    public Map<String, Object> calculateN2OFromManure(String animalType, int headCount,
                                                        String manureSystem, double annualNKg) {
        double efDirect = 0.01;
        double efIndirect = 0.005;
        double fracGASF = 0.1;
        double fracLeach = 0.23;

        double n2oDirect = annualNKg * efDirect;
        double n2oIndirect = annualNKg * (fracGASF * efIndirect + fracLeach * 0.0075);
        double totalN2O = n2oDirect + n2oIndirect;
        double co2e = totalN2O * 273.0;

        Map<String, Object> result = new HashMap<>();
        result.put("animalType", animalType);
        result.put("headCount", headCount);
        result.put("annualNInput", annualNKg);
        result.put("n2oDirect", round(n2oDirect));
        result.put("n2oIndirect", round(n2oIndirect));
        result.put("totalN2O", round(totalN2O));
        result.put("co2eKg", round(co2e));
        result.put("co2eTon", round(co2e / 1000));
        result.put("unit", "kgN2O");
        return result;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
