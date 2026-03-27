package com.gladysz.kidspartymanager.dto.currencyrate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public record CurrencyDataDto(
        Map<String, BigDecimal> currentRates,
        LocalDate effectiveDate
){}
