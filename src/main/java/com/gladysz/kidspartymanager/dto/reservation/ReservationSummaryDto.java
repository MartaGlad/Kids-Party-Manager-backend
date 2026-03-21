package com.gladysz.kidspartymanager.dto.reservation;

import com.gladysz.kidspartymanager.domain.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReservationSummaryDto(
        Long id, String eventPackageName, String animatorName,
        LocalDateTime eventDateTime,
        int childrenCount, Status status, BigDecimal price
) {}
