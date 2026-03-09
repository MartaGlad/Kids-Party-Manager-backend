package com.gladysz.kidspartymanager.dto.eventpackage;

import java.math.BigDecimal;

public record EventPackageResponseDto(Long id, String name, String description, BigDecimal basePrice,
                                      int maxChildrenCount, int durationHr) {
}
