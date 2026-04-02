package com.gladysz.kidspartymanager.service;


import com.gladysz.kidspartymanager.domain.CurrencyRate;
import com.gladysz.kidspartymanager.dto.currencyrate.CurrencyDataDto;
import com.gladysz.kidspartymanager.exception.currencyrate.CurrencyRateNotFoundException;
import com.gladysz.kidspartymanager.repository.CurrencyRateRepository;
import com.gladysz.kidspartymanager.service.integration.exchangerate.ExchangeRateProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CurrencyRateServiceTest {

    @InjectMocks
    private CurrencyRateService currencyRateService;

    @Mock
    private ExchangeRateProvider exchangeRateProvider;

    @Mock
    private CurrencyRateRepository currencyRateRepository;


    @Test
    void shouldReturnOneWhenCurrencyIsPln() {

        //Given
        String code = "PLN";

        //When
        BigDecimal rate = currencyRateService.getRateByCode(code);

        //Then
        assertEquals(BigDecimal.ONE, rate);
        verifyNoInteractions(currencyRateRepository);
    }


    @Test
    void shouldReturnRateWhenCurrencyExists() {

        //Given
        String code = "EUR";

        CurrencyRate currencyRate = new CurrencyRate();
        currencyRate.setRate(new BigDecimal("4.3"));

        when(currencyRateRepository.findByCurrencyCode(code))
                .thenReturn(Optional.of(currencyRate));

        //When
        BigDecimal rateFetched = currencyRateService.getRateByCode(code);

        //Then
        assertEquals(new BigDecimal("4.3"), rateFetched);
        verify(currencyRateRepository).findByCurrencyCode(code);
    }


    @Test
    void shouldReturnExceptionWhenCurrencyNotFound() {

        //Given
        String code = "EUR";

        when(currencyRateRepository.findByCurrencyCode(code))
                .thenReturn(Optional.empty());

        //When & Then
        assertThrows(CurrencyRateNotFoundException.class,
                () -> currencyRateService.getRateByCode(code));
    }


    @Test
    void shouldSaveFetchedRatesWhenRatesDoNotExist() {

        //Given
        Map<String, BigDecimal> currentRates = new HashMap<>();
        currentRates.put("EUR", new BigDecimal("4.3"));
        currentRates.put("USD", new BigDecimal("3.8"));
        currentRates.put("GBP", new BigDecimal("5.0"));
        LocalDate effectiveDate = LocalDate.now();

        CurrencyDataDto currencyDataDto = new CurrencyDataDto(currentRates, effectiveDate);

        when(exchangeRateProvider.getCurrentCurrencyData()).thenReturn(currencyDataDto);
        when(currencyRateRepository.findByCurrencyCode("EUR"))
                .thenReturn(Optional.empty());
        when(currencyRateRepository.findByCurrencyCode("USD"))
                .thenReturn(Optional.empty());
        when(currencyRateRepository.findByCurrencyCode("GBP"))
                .thenReturn(Optional.empty());

        //When
        currencyRateService.fetchAndSaveActualRates();

        ArgumentCaptor<CurrencyRate> captor = ArgumentCaptor.forClass(CurrencyRate.class);
        verify(currencyRateRepository, times(3)).save(captor.capture());
        List<CurrencyRate> savedCurrencyRates = captor.getAllValues();
        CurrencyRate currencyRate = savedCurrencyRates.stream()
                .filter(r -> r.getCurrencyCode().equals("EUR"))
                .findFirst().orElseThrow();

        //Then
        verify(currencyRateRepository).findByCurrencyCode("EUR");
        verify(currencyRateRepository).findByCurrencyCode("USD");
        verify(currencyRateRepository).findByCurrencyCode("GBP");
        assertEquals("EUR", currencyRate.getCurrencyCode());
        assertEquals(new BigDecimal("4.3"), currencyRate.getRate());
        assertEquals(effectiveDate, currencyRate.getEffectiveDate());
    }


    @Test
    void shouldUpdateExistingRatesWhenCurrencyExists() {

        //Given
        Map<String, BigDecimal> currentRates = new HashMap<>();
        currentRates.put("EUR", new BigDecimal("4.8"));
        LocalDate effectiveDate = LocalDate.now();

        CurrencyRate currencyRate = new CurrencyRate();
        currencyRate.setCurrencyCode("EUR");
        currencyRate.setRate(new BigDecimal("4.3"));
        currencyRate.setEffectiveDate(effectiveDate.minusDays(1));

        CurrencyDataDto currencyDataDto = new CurrencyDataDto(currentRates, effectiveDate);

        when(exchangeRateProvider.getCurrentCurrencyData()).thenReturn(currencyDataDto);

        when(currencyRateRepository.findByCurrencyCode("EUR"))
                .thenReturn(Optional.of(currencyRate));

        //When
        currencyRateService.fetchAndSaveActualRates();
        ArgumentCaptor<CurrencyRate> captor = ArgumentCaptor.forClass(CurrencyRate.class);
        verify(currencyRateRepository).save(captor.capture());
        CurrencyRate savedCurrencyRate = captor.getValue();

        //Then
        verify(currencyRateRepository).findByCurrencyCode("EUR");
        assertEquals("EUR", savedCurrencyRate.getCurrencyCode());
        assertEquals(new BigDecimal("4.8"), savedCurrencyRate.getRate());
        assertEquals(effectiveDate, savedCurrencyRate.getEffectiveDate());
    }
}
