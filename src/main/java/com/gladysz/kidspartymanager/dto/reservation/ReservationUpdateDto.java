package com.gladysz.kidspartymanager.dto.reservation;

import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;

public record ReservationUpdateDto(LocalDateTime eventDateTime, @Min(1) Integer childrenCount,
                                   @Min(1) Integer birthdayChildAge) {
}
