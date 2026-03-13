package com.gladysz.kidspartymanager.service.integration;

import com.gladysz.kidspartymanager.dto.currencyrate.CurrencyDataDto;


public interface ExchangeRateProvider {

    CurrencyDataDto getCurrentCurrencyData();
}

