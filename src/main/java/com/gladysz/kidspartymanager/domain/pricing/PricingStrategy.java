package com.gladysz.kidspartymanager.domain.pricing;

import com.gladysz.kidspartymanager.domain.EventPackage;
import java.math.BigDecimal;

public interface PricingStrategy {

    BigDecimal EXTRA_CHILD_COST = new BigDecimal("50.00");

    BigDecimal calculatePrice(EventPackage eventPackage, int childrenCount);
}
