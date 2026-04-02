package com.gladysz.kidspartymanager.service;

import com.gladysz.kidspartymanager.domain.EventPackage;
import com.gladysz.kidspartymanager.domain.pricing.HolidayPricingStrategy;
import com.gladysz.kidspartymanager.domain.pricing.PricingResult;
import com.gladysz.kidspartymanager.domain.pricing.PricingStrategy;
import com.gladysz.kidspartymanager.domain.pricing.StandardPricingStrategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class PricingService {

    private final HolidayService holidayService;
    private final StandardPricingStrategy standardPricingStrategy;
    private final HolidayPricingStrategy holidayPricingStrategy;
    private final CurrencyRateService currencyRateService;


    public PricingResult getPricingResult(EventPackage eventPackage, int childrenCount, LocalDate date) {

        boolean holiday = holidayService.isHoliday(date);

        PricingStrategy pricingStrategy = holiday ? holidayPricingStrategy : standardPricingStrategy;

        BigDecimal finalPricePln = pricingStrategy.calculatePrice(eventPackage, childrenCount);

        BigDecimal priceInEur = finalPricePln.divide(
                currencyRateService.getRateByCode("EUR"),2, RoundingMode.HALF_UP);

        BigDecimal priceInUsd = finalPricePln.divide(
                currencyRateService.getRateByCode("USD"),2, RoundingMode.HALF_UP);

        BigDecimal priceInGbp = finalPricePln.divide(
                currencyRateService.getRateByCode("GBP"),2, RoundingMode.HALF_UP);


        return new PricingResult(finalPricePln, priceInEur, priceInUsd, priceInGbp, holiday);
    }
}
