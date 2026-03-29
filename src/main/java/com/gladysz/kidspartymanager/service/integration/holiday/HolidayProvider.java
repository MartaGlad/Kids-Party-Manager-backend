package com.gladysz.kidspartymanager.service.integration.holiday;

import com.gladysz.kidspartymanager.dto.holiday.NagerDateHolidayDto;

import java.util.List;

public interface HolidayProvider {

    List<NagerDateHolidayDto> getHolidays(int year, String countryCode);
}
