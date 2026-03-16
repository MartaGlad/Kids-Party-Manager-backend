package com.gladysz.kidspartymanager.dto.animator;

import java.math.BigDecimal;

public record AnimatorRatingResponseDto(
        Long animatorId,
        BigDecimal averageRating,
        long ratingsCount
) {}
