package com.gladysz.kidspartymanager.service.pricing;

import com.gladysz.kidspartymanager.domain.EventPackage;
import com.gladysz.kidspartymanager.domain.pricing.HolidayPricingStrategy;
import com.gladysz.kidspartymanager.domain.pricing.PricingResult;
import com.gladysz.kidspartymanager.domain.pricing.StandardPricingStrategy;
import com.gladysz.kidspartymanager.service.CurrencyRateService;
import com.gladysz.kidspartymanager.service.HolidayService;
import com.gladysz.kidspartymanager.service.PricingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PricingServiceTest {

    @InjectMocks
    private PricingService pricingService;

    @Mock
    private HolidayService holidayService;

    @Mock
    private StandardPricingStrategy standardPricingStrategy;

    @Mock
    private HolidayPricingStrategy holidayPricingStrategy;

    @Mock
    private CurrencyRateService currencyRateService;


    @Test
    void shouldUseStandardPricingWhenNotHoliday() {

        //Given
        LocalDate date = LocalDate.of(2026, 1,3);
        EventPackage eventPackage = new EventPackage();
        eventPackage.setMaxChildrenCount(20);
        int childrenCount = 15;

        when(holidayService.isHoliday(date)).thenReturn(false);

        when(standardPricingStrategy.calculatePrice(eventPackage, childrenCount))
                .thenReturn(new BigDecimal("1000.00"));

        when(currencyRateService.getRateByCode("EUR")).thenReturn(new BigDecimal("4.00"));
        when(currencyRateService.getRateByCode("USD")).thenReturn(new BigDecimal("3.00"));
        when(currencyRateService.getRateByCode("GBP")).thenReturn(new BigDecimal("5.00"));

        //When
        PricingResult result = pricingService.getPricingResult(eventPackage, childrenCount, date);

        //Then
        verify(standardPricingStrategy).calculatePrice(eventPackage, childrenCount);
        verify(holidayPricingStrategy, never()).calculatePrice(any(), anyInt());
        assertFalse(result.holiday());
        assertThat(result.finalPricePln()).isEqualByComparingTo("1000.00");
        assertThat(result.priceInEur()).isEqualByComparingTo("250.00");
        assertThat(result.priceInUsd()).isEqualByComparingTo("333.33");
        assertThat(result.priceInGbp()).isEqualByComparingTo("200.00");
    }


    @Test
    void shouldUseHolidayPricingWhenHoliday() {

        //Given
        LocalDate date = LocalDate.of(2026, 4,6);
        EventPackage eventPackage = new EventPackage();
        eventPackage.setMaxChildrenCount(20);
        int childrenCount = 15;

        when(holidayService.isHoliday(date)).thenReturn(true);

        when(holidayPricingStrategy.calculatePrice(eventPackage, childrenCount))
                .thenReturn(BigDecimal.valueOf(1200.00));

        when(currencyRateService.getRateByCode("EUR")).thenReturn(new BigDecimal("4.00"));
        when(currencyRateService.getRateByCode("USD")).thenReturn(new BigDecimal("3.00"));
        when(currencyRateService.getRateByCode("GBP")).thenReturn(new BigDecimal("5.00"));

        //When
        PricingResult result = pricingService.getPricingResult(eventPackage, childrenCount, date);

        //Then
        verify(holidayPricingStrategy).calculatePrice(eventPackage, childrenCount);
        verify(standardPricingStrategy, never()).calculatePrice(any(), anyInt());
        assertTrue(result.holiday());
        assertThat(result.finalPricePln()).isEqualByComparingTo("1200.00");
        assertThat(result.priceInEur()).isEqualByComparingTo("300.00");
        assertThat(result.priceInUsd()).isEqualByComparingTo("400.00");
        assertThat(result.priceInGbp()).isEqualByComparingTo("240.00");
    }
}
