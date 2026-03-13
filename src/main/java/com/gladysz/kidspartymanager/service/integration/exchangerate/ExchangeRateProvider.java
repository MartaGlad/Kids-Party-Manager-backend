package com.gladysz.kidspartymanager.service.integration.exchangerate;

import com.gladysz.kidspartymanager.dto.currencyrate.CurrencyDataDto;


public interface ExchangeRateProvider {

    CurrencyDataDto getCurrentCurrencyData();
}

