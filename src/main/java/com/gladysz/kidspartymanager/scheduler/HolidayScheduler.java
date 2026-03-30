package com.gladysz.kidspartymanager.scheduler;

import com.gladysz.kidspartymanager.service.HolidayService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class HolidayScheduler {

    private final HolidayService holidayService;
    private static final Logger LOGGER = LoggerFactory.getLogger(HolidayScheduler.class);


    //@Scheduled(fixedRate = Long.MAX_VALUE, initialDelay = 0)
    @Scheduled(cron = "0 0 3 2 * *")
    public void updateHolidays() {

        int currentYear = LocalDate.now().getYear();

        LOGGER.info("Starting holidays update.");

        long startTime = System.currentTimeMillis();

        try {
            holidayService.deleteOldHolidays();
            holidayService.fetchAndSaveHolidaysIfMissing(currentYear);
            holidayService.fetchAndSaveHolidaysIfMissing(currentYear + 1);
            LOGGER.info("Holidays update finished.");
        } catch (Exception e) {
            LOGGER.error("Holidays update failed.", e);
        } finally {
            LOGGER.info("Duration in ms = {}", System.currentTimeMillis() - startTime);
        }
    }
}