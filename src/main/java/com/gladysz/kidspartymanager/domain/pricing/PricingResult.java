package com.gladysz.kidspartymanager.domain.pricing;

import java.math.BigDecimal;

public record PricingResult(
        BigDecimal finalPricePln,
        BigDecimal priceInEur,
        BigDecimal priceInUsd,
        BigDecimal priceInGbp,
        boolean holiday
){}
