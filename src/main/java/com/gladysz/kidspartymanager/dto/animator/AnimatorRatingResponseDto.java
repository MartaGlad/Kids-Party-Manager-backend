package com.gladysz.kidspartymanager.dto.animator;

public record AnimatorRatingResponseDto(
        Long animatorId,
        Double averageRating,
        long ratingsCount
){}
