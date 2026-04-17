package com.gladysz.kidspartymanager.service.integration.holiday;


import com.gladysz.kidspartymanager.dto.holiday.NagerDateHolidayDto;
import com.gladysz.kidspartymanager.exception.externalapi.ExternalApiException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings({"rawtypes", "unchecked"})
@ExtendWith(MockitoExtension.class)
public class NagerDateHolidayAdapterTest {

    private static final int YEAR = 2026;
    private static final String COUNTRY_CODE = "PL";
    private static final String URL = "https://date.nager.at/api/v3/PublicHolidays/{year}/{countryCode}";

    @InjectMocks
    private NagerDateHolidayAdapter nagerDateHolidayAdapter;

    @Mock
    private RestClient restClient;

    @Test
    void shouldReturnHolidaysSuccessfully() {

        //Given
        NagerDateHolidayDto dto1 = new NagerDateHolidayDto
                (LocalDate.of(2026,1, 1), "New Year");

        NagerDateHolidayDto dto2 = new NagerDateHolidayDto
                (LocalDate.of(2026,12, 25), "Christmas");

        NagerDateHolidayDto[] response = new NagerDateHolidayDto[]{dto1, dto2};

        RestClient.RequestHeadersUriSpec mock1 =  Mockito.mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec mock2 = Mockito.mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec mock3 = Mockito.mock(RestClient.ResponseSpec.class);

        when(restClient.get()).thenReturn(mock1);
        when(mock1.uri(URL, YEAR, COUNTRY_CODE)).thenReturn(mock2);
        when(mock2.retrieve()).thenReturn(mock3);
        when(mock3.body(NagerDateHolidayDto[].class)).thenReturn(response);

        //When
        List<NagerDateHolidayDto> responseList = nagerDateHolidayAdapter.getHolidays(YEAR, COUNTRY_CODE);

        //Then
        assertNotNull(responseList);
        assertEquals(2, responseList.size());
        assertEquals(LocalDate.of(2026,1, 1), responseList.getFirst().date());
        assertEquals("Christmas", responseList.get(1).name());
        verify(restClient).get();
        verify(mock1).uri(URL, YEAR, COUNTRY_CODE);
        verify(mock2).retrieve();
        verify(mock3).body(NagerDateHolidayDto[].class);
    }


    @Test
    void shouldThrowExternalApiExceptionWhenResponseIsNull() {

        //Given
        NagerDateHolidayDto[] response = null;

        RestClient.RequestHeadersUriSpec mock1 = Mockito.mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec mock2 = Mockito.mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec mock3 = Mockito.mock(RestClient.ResponseSpec.class);

        when(restClient.get()).thenReturn(mock1);
        when(mock1.uri(URL, YEAR, COUNTRY_CODE)).thenReturn(mock2);
        when(mock2.retrieve()).thenReturn(mock3);
        when(mock3.body(NagerDateHolidayDto[].class)).thenReturn(response);

        //When & Then
        ExternalApiException e = assertThrows(ExternalApiException.class,
                () -> nagerDateHolidayAdapter.getHolidays(YEAR, COUNTRY_CODE));
        assertEquals("Empty response from Nager.Date received.", e.getMessage());
        verify(restClient).get();
        verify(mock1).uri(URL, YEAR, COUNTRY_CODE);
        verify(mock2).retrieve();
        verify(mock3).body(NagerDateHolidayDto[].class);
    }


    @Test
    void shouldThrowExternalApiExceptionWhenResponseIsEmpty() {

        //Given
        NagerDateHolidayDto[] response = new NagerDateHolidayDto[0];

        RestClient.RequestHeadersUriSpec mock1 = Mockito.mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec mock2 = Mockito.mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec mock3 = Mockito.mock(RestClient.ResponseSpec.class);

        when(restClient.get()).thenReturn(mock1);
        when(mock1.uri(URL, YEAR, COUNTRY_CODE)).thenReturn(mock2);
        when(mock2.retrieve()).thenReturn(mock3);
        when(mock3.body(NagerDateHolidayDto[].class)).thenReturn(response);

        //When & Then
        ExternalApiException e = assertThrows(ExternalApiException.class,
                () -> nagerDateHolidayAdapter.getHolidays(YEAR, COUNTRY_CODE));
        assertEquals("Empty response from Nager.Date received.", e.getMessage());
        verify(restClient).get();
        verify(mock1).uri(URL, YEAR, COUNTRY_CODE);
        verify(mock2).retrieve();
        verify(mock3).body(NagerDateHolidayDto[].class);
    }


    @Test
    void shouldThrowExternalApiExceptionWhenRestClientFails() {

        //Given
        when(restClient.get()).thenThrow(new RestClientException("Error"));

        //When & Then
        ExternalApiException e = assertThrows(ExternalApiException.class,
                () ->  nagerDateHolidayAdapter.getHolidays(YEAR, COUNTRY_CODE));
        assertInstanceOf(RestClientException.class, e.getCause());
        assertEquals("Failed to fetch holiday response for the year " + YEAR + " from Nager.Date", e.getMessage());
        verify(restClient).get();
    }
}

