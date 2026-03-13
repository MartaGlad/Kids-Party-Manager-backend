package com.gladysz.kidspartymanager.scheduler;

import com.gladysz.kidspartymanager.service.CurrencyRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrencyRateScheduler {

    private final CurrencyRateService currencyRateService;

    @Scheduled(cron = "0 0 13 * * MON-FRI")
    //@Scheduled(fixedRate = 10000)
    public void updateCurrentRates() {

        currencyRateService.fetchAndSaveActualRates();
    }
}
