package com.ecocarbon.mrv.engine.agriculture;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class FertilizerN2OCalculator {
    private static final double EF1 = 0.01;
    private static final double EF2 = 0.006;
    private static final double EF3 = 0.002;
    private static final double EF4 = 0.01;
    private static final double EF5 = 0.006;
    private static final double FRAC_GASF = 0.1;
    private static final double FRAC_LEACH = 0.23;
    private static final double GWP_N2O = 273.0;

    private static final Map<String, Double> FERTILIZER_N_CONTENT = new HashMap<>() {{
        put("urea", 0.46);
        put("ammonium_bicarbonate", 0.17);
        put("ammonium_sulfate", 0.21);
        put("ammonium_nitrate", 0.34);
        put("calcium_ammonium_nitrate", 0.27);
        put("potassium_nitrate", 0.13);
        put("compound_fertilizer", 0.15);
        put("organic_fertilizer", 0.02);
        put("farmyard_manure", 0.008);
        put("compost", 0.01);
        put("green_manure", 0.005);
    }};

    public Map<String, Object> calculateDirectEmission(double syntheticNKg, double organicNKg,
                                                         double cropResidueNKg) {
        double n2oDirectSynthetic = syntheticNKg * EF1;
        double n2oDirectOrganic = organicNKg * EF2;
        double n2oDirectResidue = cropResidueNKg * EF3;
        double n2oDirect = n2oDirectSynthetic + n2oDirectOrganic + n2oDirectResidue;
        double co2e = n2oDirect * GWP_N2O;

        Map<String, Object> result = new HashMap<>();
        result.put("methodology", "IPCC_2006_Direct");
        result.put("syntheticNKg", syntheticNKg);
        result.put("organicNKg", organicNKg);
        result.put("cropResidueNKg", cropResidueNKg);
        result.put("ef1", EF1);
        result.put("ef2", EF2);
        result.put("ef3", EF3);
        result.put("n2oDirectSynthetic", round(n2oDirectSynthetic));
        result.put("n2oDirectOrganic", round(n2oDirectOrganic));
        result.put("n2oDirectResidue", round(n2oDirectResidue));
        result.put("n2oDirectTotal", round(n2oDirect));
        result.put("co2eKg", round(co2e));
        result.put("co2eTon", round(co2e / 1000));
        result.put("unit", "kgN2O");
        return result;
    }

    public Map<String, Object> calculateIndirectEmission(double syntheticNKg, double organicNKg,
                                                          double cropResidueNKg) {
        double totalNInput = syntheticNKg + organicNKg + cropResidueNKg;
        double nVolatilization = totalNInput * FRAC_GASF;
        double nLeaching = totalNInput * FRAC_LEACH;

        double n2oAtmospheric = nVolatilization * EF4;
        double n2oLeaching = nLeaching * EF5;
        double n2oIndirect = n2oAtmospheric + n2oLeaching;
        double co2e = n2oIndirect * GWP_N2O;

        Map<String, Object> result = new HashMap<>();
        result.put("methodology", "IPCC_2006_Indirect");
        result.put("totalNInput", round(totalNInput));
        result.put("fracGASF", FRAC_GASF);
        result.put("fracLeach", FRAC_LEACH);
        result.put("ef4", EF4);
        result.put("ef5", EF5);
        result.put("nVolatilization", round(nVolatilization));
        result.put("nLeaching", round(nLeaching));
        result.put("n2oAtmospheric", round(n2oAtmospheric));
        result.put("n2oLeaching", round(n2oLeaching));
        result.put("n2oIndirectTotal", round(n2oIndirect));
        result.put("co2eKg", round(co2e));
        result.put("co2eTon", round(co2e / 1000));
        result.put("unit", "kgN2O");
        return result;
    }

    public Map<String, Object> calculateTotalEmission(double syntheticNKg, double organicNKg,
                                                        double cropResidueNKg) {
        Map<String, Object> direct = calculateDirectEmission(syntheticNKg, organicNKg, cropResidueNKg);
        Map<String, Object> indirect = calculateIndirectEmission(syntheticNKg, organicNKg, cropResidueNKg);

        double directCo2e = (Double) direct.get("co2eKg");
        double indirectCo2e = (Double) indirect.get("co2eKg");
        double totalCo2e = directCo2e + indirectCo2e;

        Map<String, Object> result = new HashMap<>();
        result.put("methodology", "IPCC_2006_Total");
        result.put("directEmission", direct);
        result.put("indirectEmission", indirect);
        result.put("directCo2eKg", round(directCo2e));
        result.put("indirectCo2eKg", round(indirectCo2e));
        result.put("totalCo2eKg", round(totalCo2e));
        result.put("totalCo2eTon", round(totalCo2e / 1000));
        result.put("unit", "kgN2O");
        return result;
    }

    public Map<String, Object> calculateByFertilizerType(Map<String, Double> fertilizerAmountsKg) {
        double totalSyntheticN = 0;
        double totalOrganicN = 0;
        Map<String, Double> nContributions = new HashMap<>();

        for (Map.Entry<String, Double> entry : fertilizerAmountsKg.entrySet()) {
            String fertilizerType = entry.getKey();
            double amountKg = entry.getValue();
            double nContent = FERTILIZER_N_CONTENT.getOrDefault(fertilizerType, 0.1);
            double nAmount = amountKg * nContent;
            nContributions.put(fertilizerType, round(nAmount));

            if (isSyntheticFertilizer(fertilizerType)) {
                totalSyntheticN += nAmount;
            } else {
                totalOrganicN += nAmount;
            }
        }

        Map<String, Object> totalResult = calculateTotalEmission(totalSyntheticN, totalOrganicN, 0);

        Map<String, Object> result = new HashMap<>();
        result.put("methodology", "IPCC_2006_ByType");
        result.put("fertilizerAmountsKg", fertilizerAmountsKg);
        result.put("nContributions", nContributions);
        result.put("totalSyntheticN", round(totalSyntheticN));
        result.put("totalOrganicN", round(totalOrganicN));
        result.putAll(totalResult);
        return result;
    }

    public double getFertilizerNContent(String fertilizerType) {
        return FERTILIZER_N_CONTENT.getOrDefault(fertilizerType, 0.1);
    }

    private boolean isSyntheticFertilizer(String type) {
        return type.contains("urea") || type.contains("ammonium") ||
               type.contains("nitrate") || type.equals("compound_fertilizer");
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
