package com.gladysz.kidspartymanager.service.pricing;

import com.gladysz.kidspartymanager.domain.EventPackage;
import com.gladysz.kidspartymanager.domain.pricing.PricingStrategy;
import com.gladysz.kidspartymanager.domain.pricing.StandardPricingStrategy;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class StandardPricingStrategyTest {


    @Test
    void shouldCalculateBasePriceWhenChildrenCountIsWithinLimit() {

        //Given
        EventPackage eventPackage = new EventPackage();
        eventPackage.setMaxChildrenCount(20);
        eventPackage.setBasePrice(new BigDecimal("1000.00"));

        int childrenCount = 15;

        PricingStrategy pricingStrategy = new StandardPricingStrategy();

        //When
        BigDecimal price = pricingStrategy.calculatePrice(eventPackage, childrenCount);

        //Then
        assertThat(price).isEqualTo(new BigDecimal("1000.00"));
    }


    @Test
    void shouldCalculateExtraPriceWhenChildrenCountIsAboveLimit() {

        //Given
        EventPackage eventPackage = new EventPackage();
        eventPackage.setMaxChildrenCount(15);
        eventPackage.setBasePrice(new BigDecimal("1000.00"));

        int childrenCount = 20;

        PricingStrategy pricingStrategy = new StandardPricingStrategy();

        //When
        BigDecimal price = pricingStrategy.calculatePrice(eventPackage, childrenCount);

        //Then
        assertThat(price).isEqualTo(new BigDecimal("1250.00"));
    }


    @Test
    void shouldCalculateBasePriceWhenChildrenCountIsEqualToLimit() {

        //Given
        EventPackage eventPackage = new EventPackage();
        eventPackage.setMaxChildrenCount(20);
        eventPackage.setBasePrice(new BigDecimal("1000.00"));

        int childrenCount = 20;

        PricingStrategy pricingStrategy = new StandardPricingStrategy();

        //When
        BigDecimal price = pricingStrategy.calculatePrice(eventPackage, childrenCount);

        //Then
        assertThat(price).isEqualTo(new BigDecimal("1000.00"));
    }
}
