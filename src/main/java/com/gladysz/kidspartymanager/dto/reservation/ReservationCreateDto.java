package com.gladysz.kidspartymanager.dto.reservation;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ReservationCreateDto(@NotNull Long eventPackageId, @NotNull Long animatorId, @NotNull Long ordererId,
                                   @NotNull LocalDateTime eventDateTime, @NotNull @Min(1) Integer childrenCount,
                                   @NotNull @Min(1) Integer birthdayChildAge) {
}
