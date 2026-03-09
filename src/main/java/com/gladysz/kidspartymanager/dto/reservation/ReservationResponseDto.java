package com.gladysz.kidspartymanager.dto.reservation;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReservationResponseDto(Long id, Long eventPackageId, Long animatorId, Long ordererId,
                                     LocalDateTime eventDateTime, int childrenCount, int birthdayChildAge,
                                     BigDecimal priceSnapshot, LocalDateTime createdAt) {
}
