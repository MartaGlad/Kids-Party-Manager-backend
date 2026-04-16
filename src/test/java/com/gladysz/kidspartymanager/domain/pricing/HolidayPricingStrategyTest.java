package com.gladysz.kidspartymanager.domain.pricing;

import com.gladysz.kidspartymanager.domain.EventPackage;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class HolidayPricingStrategyTest {


    @Test
    void shouldCalculateBaseHolidayPriceWhenChildrenCountIsWithinLimit() {

        //Given
        EventPackage eventPackage = new EventPackage();
        eventPackage.setMaxChildrenCount(20);
        eventPackage.setBasePrice(new BigDecimal("1000.00"));

        int childrenCount = 15;

        PricingStrategy pricingStrategy = new HolidayPricingStrategy();

        //When
        BigDecimal price = pricingStrategy.calculatePrice(eventPackage, childrenCount);

        //Then
        assertThat(price).isEqualTo(new BigDecimal("1200.00"));
    }


    @Test
    void shouldCalculateExtraHolidayPriceWhenChildrenCountIsAboveLimit() {

        //Given
        EventPackage eventPackage = new EventPackage();
        eventPackage.setMaxChildrenCount(15);
        eventPackage.setBasePrice(new BigDecimal("1000.00"));

        int childrenCount = 20;

        PricingStrategy pricingStrategy = new HolidayPricingStrategy();

        //When
        BigDecimal price = pricingStrategy.calculatePrice(eventPackage, childrenCount);

        //Then
        assertThat(price).isEqualTo(new BigDecimal("1500.00"));
    }


    @Test
    void shouldCalculateBaseHolidayPriceWhenChildrenCountIsEqualToLimit() {

        //Given
        EventPackage eventPackage = new EventPackage();
        eventPackage.setMaxChildrenCount(20);
        eventPackage.setBasePrice(new BigDecimal("1000.00"));

        int childrenCount = 20;

        PricingStrategy pricingStrategy = new HolidayPricingStrategy();

        //When
        BigDecimal price = pricingStrategy.calculatePrice(eventPackage, childrenCount);

        //Then
        assertThat(price).isEqualTo(new BigDecimal("1200.00"));
    }
}