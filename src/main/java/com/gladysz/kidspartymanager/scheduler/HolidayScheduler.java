package com.gladysz.kidspartymanager.scheduler;

import com.gladysz.kidspartymanager.service.HolidayService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class HolidayScheduler {

    private final HolidayService holidayService;

    //@Scheduled(fixedRate = Long.MAX_VALUE, initialDelay = 0)
    @Scheduled(cron = "0 0 3 2 * *")
    public void updateHolidays() {

        int currentYear = LocalDate.now().getYear();

        holidayService.deleteOldHolidays();
        holidayService.fetchAndSaveHolidaysIfMissing(currentYear);
        holidayService.fetchAndSaveHolidaysIfMissing(currentYear + 1);
    }
}
