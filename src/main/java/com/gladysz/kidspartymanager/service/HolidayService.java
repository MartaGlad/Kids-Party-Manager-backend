package com.gladysz.kidspartymanager.service;

import com.gladysz.kidspartymanager.domain.Holiday;
import com.gladysz.kidspartymanager.dto.holiday.NagerDateHolidayDto;
import com.gladysz.kidspartymanager.repository.HolidayRepository;
import com.gladysz.kidspartymanager.service.integration.holiday.HolidayProvider;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final Logger LOGGER =  LoggerFactory.getLogger(HolidayService.class);


    public void fetchAndSaveHolidaysIfMissing(int year) {

        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        if (holidayRepository.existsByDateBetween(startDate, endDate)) {
            LOGGER.info("Holidays for the year {} already exist in the database.",  year);
            return;
        }

        LOGGER.info("Fetching holidays for the year {}.", year);

        List<NagerDateHolidayDto> holidaysDto = holidayProvider
                .getHolidays(year, "PL");

        List<Holiday> holidays = holidaysDto.stream().map(dto -> {
                    Holiday holiday = new Holiday();
                    holiday.setDate(dto.date());
                    holiday.setName(dto.name());
                    return holiday;
                }).toList();

        holidayRepository.saveAll(holidays);

        LOGGER.info("Saved {} entries for the year {}.", holidays.size(), year);
    }


    public void deleteOldHolidays() {

        LOGGER.info("Deleting holidays before {}.", Year.now().atDay(1));

        holidayRepository.deleteByDateBefore(Year.now().atDay(1));

        LOGGER.info("Old holidays deleted.");
    }


    public boolean isHoliday(LocalDate date) {

       return holidayRepository.existsByDate(date);
    }
}
