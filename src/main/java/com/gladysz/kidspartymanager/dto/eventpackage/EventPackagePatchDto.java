package com.gladysz.kidspartymanager.dto.eventpackage;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record EventPackagePatchDto(@Size(max = 100) String name,
                                   @Size(max = 500) String description,
                                   @DecimalMin("500.00") @Digits(integer = 10, fraction = 2)
                                   BigDecimal basePrice,
                                   @Min(1) Integer maxChildrenCount, @Min(1) Integer durationHr) {
}
