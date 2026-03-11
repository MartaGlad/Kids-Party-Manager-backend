package com.gladysz.kidspartymanager.domain.pricing;

import com.gladysz.kidspartymanager.domain.EventPackage;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class HolidayPricingStrategy implements PricingStrategy {

    private static final BigDecimal HOLIDAY_MULTIPLIER = new BigDecimal("1.20");

    @Override
    public BigDecimal calculatePrice(EventPackage eventPackage, int childrenCount) {

        int extraChildCount = childrenCount - eventPackage.getMaxChildrenCount();

        BigDecimal price = eventPackage.getBasePrice();

        if (extraChildCount > 0) {

             price = price.add(BigDecimal.valueOf(extraChildCount).multiply(EXTRA_CHILD_COST));

        }
        return price
                .multiply(HOLIDAY_MULTIPLIER)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
