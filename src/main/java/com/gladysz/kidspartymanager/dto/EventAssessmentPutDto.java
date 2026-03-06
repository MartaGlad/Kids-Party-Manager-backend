package com.gladysz.kidspartymanager.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EventAssessmentPutDto(
        @NotNull @Min(1) @Max(5) Integer rating, @Size(max = 600) String comment) {
}
