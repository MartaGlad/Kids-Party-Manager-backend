package com.gladysz.kidspartymanager.dto.eventassessment;

import jakarta.validation.constraints.*;

public record EventAssessmentCreateDto(
        @NotNull @Min(1) @Max(5) Integer rating,
        @Size(max = 600) String comment
){}

