package com.gladysz.kidspartymanager.dto.reservation;

import com.gladysz.kidspartymanager.domain.Status;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReservationResponseDto(Long id, Long eventPackageId, Long animatorId, Long ordererId,
                                     LocalDateTime eventDateTime, boolean holidayFlag,
                                     int childrenCount, int birthdayChildAge, Status status,
                                     BigDecimal priceSnapshot, LocalDateTime createdAt) {
}
