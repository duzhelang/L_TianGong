package com.ecocarbon.mrv.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.springframework.stereotype.Component;

@Component
public class UncertaintyAnalyzer {
    private final Random random = new Random();

    public Map<String, Object> monteCarloSimulation(double mean, double stdDev, int iterations) {
        List<Double> samples = new ArrayList<>();
        for (int i = 0; i < iterations; i++) {
            samples.add(generateNormalSample(mean, stdDev));
        }
        Collections.sort(samples);

        Map<String, Object> result = new HashMap<>();
        result.put("mean", round(calculateMean(samples)));
        result.put("stdDev", round(calculateStdDev(samples)));
        result.put("median", round(calculatePercentile(samples, 50)));
        result.put("percentile5", round(calculatePercentile(samples, 5)));
        result.put("percentile95", round(calculatePercentile(samples, 95)));
        result.put("confidenceInterval95", calculateConfidenceInterval(samples, 0.95));
        result.put("iterations", iterations);
        return result;
    }

    public Map<String, Object> analyzeEmissionUncertainty(Map<String, Double> activityDataUncertainty,
                                                          Map<String, Double> emissionFactorUncertainty,
                                                          int iterations) {
        List<Double> results = new ArrayList<>();
        for (int i = 0; i < iterations; i++) {
            double emission = 1.0;
            for (Map.Entry<String, Double> entry : activityDataUncertainty.entrySet()) {
                emission *= generateLognormalSample(entry.getValue());
            }
            for (Map.Entry<String, Double> entry : emissionFactorUncertainty.entrySet()) {
                emission *= generateLognormalSample(entry.getValue());
            }
            results.add(emission);
        }
        Collections.sort(results);

        Map<String, Object> result = new HashMap<>();
        result.put("mean", round(calculateMean(results)));
        result.put("stdDev", round(calculateStdDev(results)));
        result.put("coefficientOfVariation", round(calculateCV(results)));
        result.put("percentile5", round(calculatePercentile(results, 5)));
        result.put("percentile95", round(calculatePercentile(results, 95)));
        result.put("confidenceInterval95", calculateConfidenceInterval(results, 0.95));
        result.put("iterations", iterations);
        return result;
    }

    public Map<String, Object> propagateUncertainty(List<Map<String, Object>> componentUncertainties) {
        double totalMean = 0;
        double totalVariance = 0;

        for (Map<String, Object> component : componentUncertainties) {
            double mean = (Double) component.get("mean");
            double stdDev = (Double) component.get("stdDev");
            totalMean += mean;
            totalVariance += stdDev * stdDev;
        }

        double totalStdDev = Math.sqrt(totalVariance);
        double cv = totalMean != 0 ? totalStdDev / Math.abs(totalMean) : 0;

        Map<String, Object> result = new HashMap<>();
        result.put("totalMean", round(totalMean));
        result.put("totalStdDev", round(totalStdDev));
        result.put("coefficientOfVariation", round(cv));
        result.put("confidenceInterval95Lower", round(totalMean - 1.96 * totalStdDev));
        result.put("confidenceInterval95Upper", round(totalMean + 1.96 * totalStdDev));
        return result;
    }

    public Map<String, Object> generateUncertaintyReport(double emissionValue,
                                                          Map<String, Double> uncertaintyContributions) {
        double totalUncertainty = 0;
        for (double contribution : uncertaintyContributions.values()) {
            totalUncertainty += contribution * contribution;
        }
        totalUncertainty = Math.sqrt(totalUncertainty);

        Map<String, Object> report = new HashMap<>();
        report.put("emissionValue", round(emissionValue));
        report.put("totalUncertainty", round(totalUncertainty));
        report.put("relativeUncertainty", round(totalUncertainty / emissionValue * 100));
        report.put("confidenceInterval95", Map.of(
                "lower", round(emissionValue - 1.96 * totalUncertainty),
                "upper", round(emissionValue + 1.96 * totalUncertainty)
        ));
        report.put("uncertaintyContributions", uncertaintyContributions);
        report.put("qualityLevel", determineQualityLevel(totalUncertainty / emissionValue));
        return report;
    }

    private double generateNormalSample(double mean, double stdDev) {
        double u1 = random.nextDouble();
        double u2 = random.nextDouble();
        double z = Math.sqrt(-2 * Math.log(u1)) * Math.cos(2 * Math.PI * u2);
        return mean + stdDev * z;
    }

    private double generateLognormalSample(double geometricStdDev) {
        double logMean = 0;
        double logStdDev = Math.log(geometricStdDev);
        double u1 = random.nextDouble();
        double u2 = random.nextDouble();
        double z = Math.sqrt(-2 * Math.log(u1)) * Math.cos(2 * Math.PI * u2);
        return Math.exp(logMean + logStdDev * z);
    }

    private double calculateMean(List<Double> values) {
        return values.stream().mapToDouble(Double::doubleValue).average().orElse(0);
    }

    private double calculateStdDev(List<Double> values) {
        double mean = calculateMean(values);
        double variance = values.stream()
                .mapToDouble(v -> Math.pow(v - mean, 2))
                .average()
                .orElse(0);
        return Math.sqrt(variance);
    }

    private double calculateCV(List<Double> values) {
        double mean = calculateMean(values);
        double stdDev = calculateStdDev(values);
        return mean != 0 ? stdDev / Math.abs(mean) : 0;
    }

    private double calculatePercentile(List<Double> sortedValues, double percentile) {
        int index = (int) Math.ceil(percentile / 100.0 * sortedValues.size()) - 1;
        return sortedValues.get(Math.max(0, Math.min(index, sortedValues.size() - 1)));
    }

    private Map<String, Double> calculateConfidenceInterval(List<Double> sortedValues, double confidence) {
        double alpha = (1 - confidence) / 2;
        double lower = calculatePercentile(sortedValues, alpha * 100);
        double upper = calculatePercentile(sortedValues, (1 - alpha) * 100);
        Map<String, Double> interval = new HashMap<>();
        interval.put("lower", round(lower));
        interval.put("upper", round(upper));
        return interval;
    }

    private String determineQualityLevel(double relativeUncertainty) {
        if (relativeUncertainty <= 0.1) {
            return "HIGH";
        } else if (relativeUncertainty <= 0.3) {
            return "MEDIUM";
        } else if (relativeUncertainty <= 0.5) {
            return "LOW";
        } else {
            return "VERY_LOW";
        }
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
