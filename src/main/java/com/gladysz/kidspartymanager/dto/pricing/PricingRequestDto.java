package com.gladysz.kidspartymanager.dto.pricing;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record PricingRequestDto(
        @NotNull Long eventPackageId, @Min(1) int childrenCount,
        @NotNull LocalDate date
){}
