package com.gladysz.kidspartymanager.dto.reservation;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ReservationCheckAvailabilityDto(
        @NotNull Long eventPackageId,
        @NotNull LocalDateTime eventDateTime
){}
