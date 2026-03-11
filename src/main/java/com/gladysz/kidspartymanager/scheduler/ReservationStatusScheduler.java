package com.gladysz.kidspartymanager.scheduler;

import com.gladysz.kidspartymanager.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class ReservationStatusScheduler {

    private final ReservationService reservationService;

    @Scheduled(cron = "0 0 * * * *")
    public void updateReservationStatuses() {

        reservationService.cancelExpiredReservations();
        reservationService.completeFinishedReservations();
    }
}
