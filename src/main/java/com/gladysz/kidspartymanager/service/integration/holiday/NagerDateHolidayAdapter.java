package com.gladysz.kidspartymanager.service.integration.holiday;

import com.gladysz.kidspartymanager.dto.holiday.NagerDateHolidayDto;
import com.gladysz.kidspartymanager.exception.externalapi.ExternalApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class NagerDateHolidayAdapter implements HolidayProvider {

    private final RestClient restClient;

    @Override
    public List<NagerDateHolidayDto> getHolidays(int year, String countryCode) {

        try {
            NagerDateHolidayDto[] response = restClient.get()
                    .uri(
                            "https://date.nager.at/api/v3/PublicHolidays/{year}/{countryCode}",
                            year,
                            countryCode
                    )
                    .retrieve()
                    .body(NagerDateHolidayDto[].class);

            if (response == null) {
                throw new ExternalApiException("No response from Nager.Date received.");
            }

            return Arrays.stream(response).toList();

        } catch (RestClientException e) {
            throw new ExternalApiException("Failed to fetch holiday response from Nager.Date", e);
        }
    }
}
