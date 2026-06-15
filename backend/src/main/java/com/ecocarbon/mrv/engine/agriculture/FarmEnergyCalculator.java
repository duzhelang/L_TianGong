package com.ecocarbon.mrv.engine.agriculture;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class FarmEnergyCalculator {
    private static final Map<String, Double> ELECTRICITY_EF = Map.of(
            "national_grid", 0.5703,
            "coal_power", 0.8842,
            "natural_gas_power", 0.4284,
            "renewable", 0.0,
            "solar", 0.0,
            "wind", 0.0,
            "hybrid", 0.35
    );

    private static final Map<String, Double> FUEL_EF = Map.of(
            "diesel", 3.17,
            "gasoline", 2.93,
            "natural_gas", 2.16,
            "lpg", 2.99,
            "fuel_oil", 3.07,
            "coal", 2.66,
            "biomass", 0.0,
            "ethanol", 1.91,
            "biodiesel", 2.54
    );

    private static final Map<String, Double> FUEL_DENSITY = Map.of(
            "diesel", 0.85,
            "gasoline", 0.75,
            "natural_gas", 0.72,
            "lpg", 0.55,
            "fuel_oil", 0.92
    );

    private static final Map<String, Double> MACHINERY_EF = Map.of(
            "tractor_small", 15.5,
            "tractor_medium", 28.3,
            "tractor_large", 52.7,
            "combine_harvester", 45.0,
            "rice_transplanter", 8.5,
            "sprayer", 5.2,
            "thresher", 12.8,
            "pump_set", 9.6
    );

    public Map<String, Object> calculateIrrigationEmission(double electricityKwh,
                                                              String electricitySource,
                                                              double irrigationAreaHa) {
        double ef = ELECTRICITY_EF.getOrDefault(electricitySource, 0.5703);
        double emissionKg = electricityKwh * ef;
        double emissionPerHa = irrigationAreaHa > 0 ? emissionKg / irrigationAreaHa : 0;

        Map<String, Object> result = new HashMap<>();
        result.put("category", "irrigation");
        result.put("electricityKwh", electricityKwh);
        result.put("electricitySource", electricitySource);
        result.put("emissionFactor", ef);
        result.put("emissionKgCO2e", round(emissionKg));
        result.put("emissionTonCO2e", round(emissionKg / 1000));
        result.put("emissionPerHa", round(emissionPerHa));
        result.put("irrigationAreaHa", irrigationAreaHa);
        result.put("unit", "kgCO2e");
        return result;
    }

    public Map<String, Object> calculateMachineryEmission(String machineryType,
                                                             double operatingHours,
                                                             double fuelConsumptionLiters,
                                                             String fuelType) {
        double fuelEf = FUEL_EF.getOrDefault(fuelType, 3.17);
        double fuelEmission = fuelConsumptionLiters * fuelEf;

        double machineryEf = MACHINERY_EF.getOrDefault(machineryType, 20.0);
        double machineryEmission = operatingHours * machineryEf;

        double totalEmission = fuelEmission + machineryEmission;

        Map<String, Object> result = new HashMap<>();
        result.put("category", "machinery");
        result.put("machineryType", machineryType);
        result.put("operatingHours", operatingHours);
        result.put("fuelType", fuelType);
        result.put("fuelConsumptionLiters", fuelConsumptionLiters);
        result.put("fuelEmissionKgCO2e", round(fuelEmission));
        result.put("machineryEmissionKgCO2e", round(machineryEmission));
        result.put("totalEmissionKgCO2e", round(totalEmission));
        result.put("totalEmissionTonCO2e", round(totalEmission / 1000));
        result.put("unit", "kgCO2e");
        return result;
    }

    public Map<String, Object> calculateFuelEmission(double fuelAmount, String fuelType,
                                                       String fuelUnit) {
        double fuelEf = FUEL_EF.getOrDefault(fuelType, 3.17);
        double amountInLiters = fuelAmount;

        if ("kg".equals(fuelUnit)) {
            double density = FUEL_DENSITY.getOrDefault(fuelType, 0.85);
            amountInLiters = fuelAmount / density;
        } else if ("gallon".equals(fuelUnit)) {
            amountInLiters = fuelAmount * 3.785;
        }

        double emissionKg = amountInLiters * fuelEf;

        Map<String, Object> result = new HashMap<>();
        result.put("category", "fuel");
        result.put("fuelType", fuelType);
        result.put("fuelAmount", fuelAmount);
        result.put("fuelUnit", fuelUnit);
        result.put("amountInLiters", round(amountInLiters));
        result.put("emissionFactor", fuelEf);
        result.put("emissionKgCO2e", round(emissionKg));
        result.put("emissionTonCO2e", round(emissionKg / 1000));
        result.put("unit", "kgCO2e");
        return result;
    }

    public Map<String, Object> calculateTotalFarmEmission(double electricityKwh,
                                                            String electricitySource,
                                                            double irrigationAreaHa,
                                                            Map<String, Map<String, Object>> machineryEmissions,
                                                            Map<String, Map<String, Object>> fuelEmissions) {
        Map<String, Object> irrigation = calculateIrrigationEmission(electricityKwh, electricitySource, irrigationAreaHa);
        double irrigationEmission = (Double) irrigation.get("emissionKgCO2e");

        double machineryTotal = 0;
        for (Map<String, Object> m : machineryEmissions.values()) {
            machineryTotal += (Double) m.getOrDefault("totalEmissionKgCO2e", 0.0);
        }

        double fuelTotal = 0;
        for (Map<String, Object> f : fuelEmissions.values()) {
            fuelTotal += (Double) f.getOrDefault("emissionKgCO2e", 0.0);
        }

        double totalEmission = irrigationEmission + machineryTotal + fuelTotal;

        Map<String, Object> result = new HashMap<>();
        result.put("irrigation", irrigation);
        result.put("machineryEmissions", machineryEmissions);
        result.put("fuelEmissions", fuelEmissions);
        result.put("irrigationTotal", round(irrigationEmission));
        result.put("machineryTotal", round(machineryTotal));
        result.put("fuelTotal", round(fuelTotal));
        result.put("totalEmissionKgCO2e", round(totalEmission));
        result.put("totalEmissionTonCO2e", round(totalEmission / 1000));
        result.put("unit", "kgCO2e");
        return result;
    }

    public double getElectricityEF(String source) {
        return ELECTRICITY_EF.getOrDefault(source, 0.5703);
    }

    public double getFuelEF(String fuelType) {
        return FUEL_EF.getOrDefault(fuelType, 3.17);
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
