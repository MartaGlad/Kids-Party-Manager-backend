package com.gladysz.kidspartymanager.service.integration.holiday;

import com.gladysz.kidspartymanager.dto.holiday.NagerDateHolidayDto;
import com.gladysz.kidspartymanager.exception.externalapi.ExternalApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.LocalDate;
import java.util.stream.Stream;


@Component
@RequiredArgsConstructor
public class NagerDateHolidayAdapter implements HolidayProvider {

    private final RestClient restClient;

    @Override
    public boolean isHoliday(LocalDate date, String countryCode) {

        try {
            NagerDateHolidayDto[] response = restClient.get()
                    .uri("https://date.nager.at/api/v3/PublicHolidays/{year}/{countryCode}", date.getYear(), countryCode)
                    .retrieve()
                    .body(NagerDateHolidayDto[].class);

            if (response == null) {
                throw new ExternalApiException("No response from Nager.Date received.");
            }

            return Stream.of(response)
                    .anyMatch(holiday -> holiday.date().equals(date));

        } catch (RestClientException e) {
            throw new ExternalApiException("Failed to fetch holiday response from Nager.Date", e);
        }
    }
}
