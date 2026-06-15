package com.ecocarbon.mrv.dto;

import jakarta.validation.constraints.NotBlank;

public record BiomassEstimationRequest(
        @NotBlank String areaId,
        @NotBlank String vegetationType,
        Double dbh,
        Double treeHeight,
        Double ndvi
) {
}
