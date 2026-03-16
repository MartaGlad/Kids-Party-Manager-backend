package com.gladysz.kidspartymanager.dto.reservation;

import com.gladysz.kidspartymanager.domain.Status;
import jakarta.validation.constraints.NotNull;

public record ReservationChangeStatusDto(@NotNull Status status){}
