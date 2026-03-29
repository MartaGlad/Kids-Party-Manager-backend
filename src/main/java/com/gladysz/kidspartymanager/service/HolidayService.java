package com.gladysz.kidspartymanager.service;

import com.gladysz.kidspartymanager.domain.Holiday;
import com.gladysz.kidspartymanager.dto.holiday.NagerDateHolidayDto;
import com.gladysz.kidspartymanager.repository.HolidayRepository;
import com.gladysz.kidspartymanager.service.integration.holiday.HolidayProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class HolidayService {

    private final HolidayProvider holidayProvider;
    private final HolidayRepository holidayRepository;


    public void fetchAndSaveHolidaysIfMissing(int year) {

        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        if(holidayRepository.existsByDateBetween(startDate, endDate)) {
            return;
        }

        List<NagerDateHolidayDto> holidaysDto = holidayProvider
                .getHolidays(year, "PL");

        List<Holiday> holidays = holidaysDto.stream().map(dto -> {
                    Holiday holiday = new Holiday();
                    holiday.setDate(dto.date());
                    holiday.setName(dto.name());
                    return holiday;
                }).toList();

        holidayRepository.saveAll(holidays);
    }


    public void deleteOldHolidays() {

        holidayRepository.deleteByDateBefore(Year.now().atDay(1));
    }


    public boolean isHoliday(LocalDate date) {

       return holidayRepository.existsByDate(date);
    }
}
