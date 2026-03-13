package com.gladysz.kidspartymanager.service.integration.holiday;

import java.time.LocalDate;

public interface HolidayProvider {

    boolean isHoliday(LocalDate date, String countryCode);
}
