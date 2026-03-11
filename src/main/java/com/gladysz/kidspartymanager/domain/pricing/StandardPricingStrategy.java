package com.gladysz.kidspartymanager.domain.pricing;

import com.gladysz.kidspartymanager.domain.EventPackage;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class StandardPricingStrategy implements PricingStrategy {

    @Override
    public BigDecimal calculatePrice(EventPackage eventPackage, int childrenCount) {

        int extraChildCount = childrenCount - eventPackage.getMaxChildrenCount();

        if (extraChildCount > 0) {

            return eventPackage.getBasePrice()
                    .add(BigDecimal.valueOf(extraChildCount).multiply(EXTRA_CHILD_COST))
                    .setScale(2, RoundingMode.HALF_UP);
        }
        return eventPackage.getBasePrice();
    }
}