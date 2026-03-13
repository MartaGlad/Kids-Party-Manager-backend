package com.gladysz.kidspartymanager.service;

import com.gladysz.kidspartymanager.domain.CurrencyRate;
import com.gladysz.kidspartymanager.dto.currencyrate.CurrencyDataDto;
import com.gladysz.kidspartymanager.exception.currencyrate.CurrencyRateNotFoundException;
import com.gladysz.kidspartymanager.repository.CurrencyRateRepository;
import com.gladysz.kidspartymanager.service.integration.ExchangeRateProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class CurrencyRateService {

    private final ExchangeRateProvider exchangeRateProvider;
    private final CurrencyRateRepository currencyRateRepository;


    @Transactional(readOnly = true)
    public BigDecimal getRateByCode(String code) {

        if ("PLN".equalsIgnoreCase(code)) {
            return BigDecimal.ONE;
        }

        return currencyRateRepository.findByCurrencyCode(code.toUpperCase())
                .map(CurrencyRate::getRate)
                .orElseThrow(() -> new CurrencyRateNotFoundException(code));
    }


    public void fetchAndSaveActualRates() {

        CurrencyDataDto currencyDataDto = exchangeRateProvider.getCurrentCurrencyData();

        Map<String, BigDecimal> rates = currencyDataDto.currentRates();
        LocalDate effectiveDate = currencyDataDto.effectiveDate();

        for (Map.Entry<String, BigDecimal> entry : rates.entrySet()) {

            String code = entry.getKey();
            BigDecimal rate = entry.getValue();

            CurrencyRate currencyRate = currencyRateRepository
                    .findByCurrencyCode(code)
                    .orElseGet(CurrencyRate::new);

            currencyRate.setCurrencyCode(code);
            currencyRate.setRate(rate);
            currencyRate.setEffectiveDate(effectiveDate);

            currencyRateRepository.save(currencyRate);
        }
    }
}
