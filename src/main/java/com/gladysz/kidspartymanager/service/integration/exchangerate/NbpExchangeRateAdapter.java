package com.gladysz.kidspartymanager.service.integration.exchangerate;


import com.gladysz.kidspartymanager.dto.currencyrate.CurrencyDataDto;
import com.gladysz.kidspartymanager.dto.currencyrate.NbpRateDto;
import com.gladysz.kidspartymanager.dto.currencyrate.NbpTableDto;
import com.gladysz.kidspartymanager.exception.externalapi.ExternalApiException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class NbpExchangeRateAdapter implements ExchangeRateProvider {

    private final RestClient restClient;
    private final static Logger LOGGER = LoggerFactory.getLogger(NbpExchangeRateAdapter.class);


    @Override
    public CurrencyDataDto getCurrentCurrencyData() {

        try {
            LOGGER.info("Sending request to NBP");

            NbpTableDto[] response = restClient.get()
                    .uri("https://api.nbp.pl/api/exchangerates/tables/A?format=json")
                    .retrieve()
                    .body(NbpTableDto[].class);

            if (response == null || response.length == 0) {
                throw new ExternalApiException("Empty response from NBP received.");
            }

            NbpTableDto tableA = response[0];

            List<NbpRateDto> rates = tableA.rates();
            LocalDate effectiveDate = tableA.effectiveDate();

            LOGGER.info("Response from NBP received, effectiveDate = {}", effectiveDate);

            Set<String> requiredCodes = Set.of("EUR", "USD", "GBP");

            Map<String, BigDecimal> selectedRatesMap = rates.stream()
                    .filter(rate -> requiredCodes.contains(rate.code()))
                    .collect(Collectors.toMap(NbpRateDto::code, NbpRateDto::mid));

            if (!selectedRatesMap.keySet().containsAll(requiredCodes)) {
                throw new ExternalApiException("Missing required codes in NBP response.");
            }

            return new CurrencyDataDto(selectedRatesMap, effectiveDate);

        } catch (RestClientException e) {
            LOGGER.error("Failed to fetch rates from NBP", e);
            throw new ExternalApiException("Failed to fetch rates from NBP.", e);
        }
    }
}
