package com.gladysz.kidspartymanager.dto;

import java.math.BigDecimal;

public record EventPackageResponseDto(Long id, String name, String description, BigDecimal basePrice,
                                      int maxChildrenCount, int durationHr) {
}
