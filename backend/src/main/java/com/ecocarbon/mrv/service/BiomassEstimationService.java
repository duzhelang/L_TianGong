package com.ecocarbon.mrv.service;

import com.ecocarbon.mrv.dto.BiomassEstimationRequest;
import com.ecocarbon.mrv.dto.BiomassEstimationResponse;
import com.ecocarbon.mrv.entity.RemoteSensingIndex;
import com.ecocarbon.mrv.repository.RemoteSensingIndexRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BiomassEstimationService {
    private final RemoteSensingIndexRepository remoteSensingIndexRepository;

    private static final double FOREST_A_COEFFICIENT = 0.0509;
    private static final double FOREST_B_COEFFICIENT = 2.4232;
    private static final double GRASSLAND_NDVI_COEFFICIENT = 1000.0;
    private static final double CROP_NDVI_COEFFICIENT = 800.0;
    private static final double CARBON_CONTENT_RATIO = 0.47;
    private static final double BIOMASS_TO_CO2_RATIO = 3.67;

    public BiomassEstimationResponse estimateBiomass(BiomassEstimationRequest request) {
        double biomass;
        double carbonStock;
        double co2Equivalent;
        String method;

        switch (request.vegetationType().toUpperCase()) {
            case "FOREST":
                biomass = estimateForestBiomass(request.dbh(), request.treeHeight());
                method = "ALLOMETRIC_EQUATION";
                break;
            case "GRASSLAND":
                biomass = estimateGrasslandBiomass(request.areaId());
                method = "NDVI_BASED";
                break;
            case "CROP":
                biomass = estimateCropBiomass(request.areaId());
                method = "NDVI_BASED";
                break;
            default:
                biomass = estimateForestBiomass(request.dbh(), request.treeHeight());
                method = "ALLOMETRIC_EQUATION";
        }

        carbonStock = biomass * CARBON_CONTENT_RATIO;
        co2Equivalent = carbonStock * BIOMASS_TO_CO2_RATIO;

        return new BiomassEstimationResponse(
                biomass,
                "t/ha",
                carbonStock,
                "tC/ha",
                co2Equivalent,
                "tCO2/ha",
                request.vegetationType(),
                method,
                LocalDateTime.now()
        );
    }

    private double estimateForestBiomass(Double dbh, Double treeHeight) {
        if (dbh == null || dbh <= 0) {
            dbh = 10.0;
        }
        if (treeHeight == null || treeHeight <= 0) {
            treeHeight = 10.0;
        }
        return FOREST_A_COEFFICIENT * Math.pow(dbh, FOREST_B_COEFFICIENT);
    }

    private double estimateGrasslandBiomass(String areaId) {
        List<RemoteSensingIndex> ndviList = remoteSensingIndexRepository.findByAreaIdAndIndexType(areaId, "NDVI");
        if (ndviList.isEmpty()) {
            return 0.0;
        }
        double avgNDVI = ndviList.stream()
                .mapToDouble(RemoteSensingIndex::getValue)
                .average()
                .orElse(0.0);
        return avgNDVI * GRASSLAND_NDVI_COEFFICIENT;
    }

    private double estimateCropBiomass(String areaId) {
        List<RemoteSensingIndex> ndviList = remoteSensingIndexRepository.findByAreaIdAndIndexType(areaId, "NDVI");
        if (ndviList.isEmpty()) {
            return 0.0;
        }
        double avgNDVI = ndviList.stream()
                .mapToDouble(RemoteSensingIndex::getValue)
                .average()
                .orElse(0.0);
        return avgNDVI * CROP_NDVI_COEFFICIENT;
    }

    public double calculateCarbonSequestration(double biomassIncrease) {
        return biomassIncrease * CARBON_CONTENT_RATIO * BIOMASS_TO_CO2_RATIO;
    }
}
