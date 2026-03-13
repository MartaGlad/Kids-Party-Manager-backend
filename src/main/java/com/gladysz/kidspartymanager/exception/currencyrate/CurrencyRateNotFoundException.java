package com.gladysz.kidspartymanager.exception.currencyrate;

public class CurrencyRateNotFoundException extends RuntimeException {

    public CurrencyRateNotFoundException(String code) {

        super("Currency rate not found for code: " + code + ".");
    }
}
