package com.gladysz.kidspartymanager.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record EventPackagePutDto(@NotBlank @Size(max = 100) String name,
                                 @NotBlank @Size(max = 500) String description,
                                 @NotNull @DecimalMin("500.00") @Digits(integer = 10, fraction = 2)
                                 BigDecimal basePrice,
                                 @NotNull @Min(1) Integer maxChildrenCount,
                                 @NotNull @Min(1) Integer durationHr) {
}
