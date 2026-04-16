package com.gladysz.kidspartymanager.service.integration;

import com.gladysz.kidspartymanager.dto.currencyrate.CurrencyDataDto;
import com.gladysz.kidspartymanager.dto.currencyrate.NbpRateDto;
import com.gladysz.kidspartymanager.dto.currencyrate.NbpTableDto;
import com.gladysz.kidspartymanager.exception.animator.AnimatorInactiveException;
import com.gladysz.kidspartymanager.exception.animator.AnimatorNotFoundException;
import com.gladysz.kidspartymanager.exception.externalapi.ExternalApiException;
import com.gladysz.kidspartymanager.service.integration.exchangerate.NbpExchangeRateAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class NbpExchangeRateAdapterTest {

    @InjectMocks
    private NbpExchangeRateAdapter nbpExchangeRateAdapter;

    @Mock
    private RestClient restClient;

    @Test
    void shouldReturnCurrencyDataForRequiredCodes() {

        //Given
        LocalDate effectiveDate = LocalDate.of(2026, 4, 16);
        List<NbpRateDto> rates = new ArrayList<>();
        rates.add(new NbpRateDto("EUR", new BigDecimal("4.24")));
        rates.add(new NbpRateDto("USD", new BigDecimal("3.60")));
        rates.add(new NbpRateDto("GBP", new BigDecimal("4.87")));
        rates.add(new NbpRateDto("RUB", new BigDecimal("0.047")));

        NbpTableDto[] response = new NbpTableDto[1];
        response[0] = new NbpTableDto(effectiveDate, rates);

        RestClient.RequestHeadersUriSpec mock1 = Mockito.mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec mock2 = Mockito.mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec mock3 = Mockito.mock(RestClient.ResponseSpec.class);

        when(restClient.get()).thenReturn(mock1);
        when(mock1.uri("https://api.nbp.pl/api/exchangerates/tables/A?format=json")).thenReturn(mock2);
        when(mock2.retrieve()).thenReturn(mock3);
        when(mock3.body(NbpTableDto[].class)).thenReturn(response);

        //When
        CurrencyDataDto currencyDataDto = nbpExchangeRateAdapter.getCurrentCurrencyData();

        //Then
        assertNotNull(currencyDataDto);
        assertEquals(LocalDate.of(2026, 4, 16), currencyDataDto.effectiveDate());
        assertEquals(3, currencyDataDto.selectedRates().size());
        assertTrue(currencyDataDto.selectedRates().containsKey("EUR"));
        assertTrue(currencyDataDto.selectedRates().containsKey("USD"));
        assertTrue(currencyDataDto.selectedRates().containsKey("GBP"));
        assertFalse(currencyDataDto.selectedRates().containsKey("RUB"));
        assertEquals(new BigDecimal("4.24"), currencyDataDto.selectedRates().get("EUR"));
        assertEquals(new BigDecimal("3.60"), currencyDataDto.selectedRates().get("USD"));
        assertEquals(new BigDecimal("4.87"), currencyDataDto.selectedRates().get("GBP"));
        verify(restClient).get();
        verify(mock1).uri("https://api.nbp.pl/api/exchangerates/tables/A?format=json");
        verify(mock2).retrieve();
        verify(mock3).body(NbpTableDto[].class);
    }


    @Test
    void shouldThrowExternalApiExceptionWhenResponseIsEmpty() {

        //Given
        NbpTableDto[] response = null;

        RestClient.RequestHeadersUriSpec mock1 = Mockito.mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec mock2 = Mockito.mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec mock3 = Mockito.mock(RestClient.ResponseSpec.class);

        when(restClient.get()).thenReturn(mock1);
        when(mock1.uri("https://api.nbp.pl/api/exchangerates/tables/A?format=json")).thenReturn(mock2);
        when(mock2.retrieve()).thenReturn(mock3);
        when(mock3.body(NbpTableDto[].class)).thenReturn(response);

        //When & Then
        ExternalApiException e = assertThrows(ExternalApiException.class,
                () -> nbpExchangeRateAdapter.getCurrentCurrencyData());
        assertEquals("Empty response from NBP received.", e.getMessage());
        verify(restClient).get();
        verify(mock1).uri("https://api.nbp.pl/api/exchangerates/tables/A?format=json");
        verify(mock2).retrieve();
        verify(mock3).body(NbpTableDto[].class);
    }


    @Test
    void shouldThrowExternalApiExceptionWhenRequiredCodesAreMissing() {

        //Given - lack of USD
        LocalDate effectiveDate = LocalDate.of(2026, 4, 16);
        List<NbpRateDto> rates = new ArrayList<>();
        rates.add(new NbpRateDto("EUR", new BigDecimal("4.24")));
        rates.add(new NbpRateDto("GBP", new BigDecimal("4.87")));

        NbpTableDto[] response = new NbpTableDto[1];
        response[0] = new NbpTableDto(effectiveDate, rates);

        RestClient.RequestHeadersUriSpec mock1 = Mockito.mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec mock2 = Mockito.mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec mock3 = Mockito.mock(RestClient.ResponseSpec.class);

        when(restClient.get()).thenReturn(mock1);
        when(mock1.uri("https://api.nbp.pl/api/exchangerates/tables/A?format=json")).thenReturn(mock2);
        when(mock2.retrieve()).thenReturn(mock3);
        when(mock3.body(NbpTableDto[].class)).thenReturn(response);

        //When & Then
        ExternalApiException e = assertThrows(ExternalApiException.class,
                () -> nbpExchangeRateAdapter.getCurrentCurrencyData());
        assertEquals("Missing required codes in NBP response.", e.getMessage());
        verify(restClient).get();
        verify(mock1).uri("https://api.nbp.pl/api/exchangerates/tables/A?format=json");
        verify(mock2).retrieve();
        verify(mock3).body(NbpTableDto[].class);
    }


    @Test
    void shouldThrowExternalApiExceptionWhenRestClientFails() {

        //Given
        when(restClient.get()).thenThrow(new RestClientException(""));

        //When & Then
        ExternalApiException e = assertThrows(ExternalApiException.class,
                () -> nbpExchangeRateAdapter.getCurrentCurrencyData());
        assertInstanceOf(RestClientException.class, e.getCause());
        assertEquals("Failed to fetch rates from NBP.", e.getMessage());
        verify(restClient).get();
    }
}

