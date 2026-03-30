package com.gladysz.kidspartymanager.service.integration.holiday;

import com.gladysz.kidspartymanager.dto.holiday.NagerDateHolidayDto;
import com.gladysz.kidspartymanager.exception.externalapi.ExternalApiException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class NagerDateHolidayAdapter implements HolidayProvider {

    private final RestClient restClient;
    private static final Logger LOGGER = LoggerFactory.getLogger(NagerDateHolidayAdapter.class);

    @Override
    public List<NagerDateHolidayDto> getHolidays(int year, String countryCode) {

        try {
            LOGGER.info("Sending request to Nager.Date.");

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

            LOGGER.info("Response from Nager.Date for the year {} received.", year);

            return Arrays.stream(response).toList();

        } catch (RestClientException e) {
            LOGGER.error("Failed to fetch holiday response for the year {} from Nager.Date.", year, e);
            throw new ExternalApiException("Failed to fetch holiday response for the year " + year + " from Nager.Date", e);
        }
    }
}
