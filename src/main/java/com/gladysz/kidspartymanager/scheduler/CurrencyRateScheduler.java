package com.gladysz.kidspartymanager.scheduler;

import com.gladysz.kidspartymanager.service.CurrencyRateService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class CurrencyRateScheduler {

    private final CurrencyRateService currencyRateService;
    private static final Logger LOGGER = LoggerFactory.getLogger(CurrencyRateScheduler.class);

    //@Scheduled(fixedRate = Long.MAX_VALUE, initialDelay = 0)
    @Scheduled(cron = "0 0 13 * * MON-FRI")
    public void updateCurrentRates() {

        LOGGER.info("Starting currency rates synchronization.");

        long startTime = System.currentTimeMillis();

        try {
            currencyRateService.fetchAndSaveActualRates();
            LOGGER.info("Currency rates synchronization finished.");
        } catch (Exception e) {
            LOGGER.error("Currency rates synchronization failed.", e);
        } finally {
            LOGGER.info("Duration in ms = {}", System.currentTimeMillis() - startTime);
        }
    }
}
