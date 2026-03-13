package com.gladysz.kidspartymanager.service;

import com.gladysz.kidspartymanager.service.integration.holiday.HolidayProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class HolidayService {

    private final HolidayProvider holidayProvider;


    public boolean isHoliday(LocalDate date) {

       return holidayProvider.isHoliday(date, "PL");
    }
}
