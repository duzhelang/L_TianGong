package com.ecocarbon.mrv.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateProjectRequest(
        @NotBlank String name,
        @NotBlank String sceneType,
        @NotBlank String areaName,
        @NotNull Double areaHa
) {
}
