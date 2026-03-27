package com.gladysz.kidspartymanager.dto.eventassessment;

import java.time.LocalDateTime;

public record EventAssessmentResponseDto(
        Long id, Long reservationId, Integer rating,
        String comment, LocalDateTime createdAt
){}




