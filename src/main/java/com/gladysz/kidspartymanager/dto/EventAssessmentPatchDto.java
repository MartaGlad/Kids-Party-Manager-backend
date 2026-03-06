package com.gladysz.kidspartymanager.dto;

import jakarta.validation.constraints.*;

public record EventAssessmentPatchDto(
        @Min(1) @Max(5) Integer rating, @Size(max = 600) String comment) {
}




