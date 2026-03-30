package com.gladysz.kidspartymanager.scheduler;

import com.gladysz.kidspartymanager.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class ReservationStatusScheduler {

    private final ReservationService reservationService;
    private static final Logger LOGGER = LoggerFactory.getLogger(ReservationStatusScheduler.class);

    //@Scheduled(fixedRate = Long.MAX_VALUE, initialDelay = 0)
    @Scheduled(cron = "0 0 * * * *")
    public void updateReservationStatuses() {

        LOGGER.info("Starting reservation statuses update.");

        long startTime = System.currentTimeMillis();

        try {
            reservationService.cancelExpiredReservations();
            reservationService.completeFinishedReservations();

            LOGGER.info("Reservation statuses update finished.");

        } catch (Exception e) {
            LOGGER.error("Reservation statuses update failed.", e);
        } finally {
            LOGGER.info("Duration in ms = {}",  System.currentTimeMillis() - startTime);
        }
    }
}
