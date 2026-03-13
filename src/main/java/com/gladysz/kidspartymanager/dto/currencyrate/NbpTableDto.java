package com.gladysz.kidspartymanager.dto.currencyrate;

import java.time.LocalDate;
import java.util.List;

public record NbpTableDto(LocalDate effectiveDate, List<NbpRateDto> rates) {
}
