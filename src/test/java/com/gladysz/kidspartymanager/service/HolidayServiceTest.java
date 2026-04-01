package com.gladysz.kidspartymanager.service;

import com.gladysz.kidspartymanager.domain.Holiday;
import com.gladysz.kidspartymanager.dto.holiday.NagerDateHolidayDto;
import com.gladysz.kidspartymanager.repository.HolidayRepository;
import com.gladysz.kidspartymanager.service.integration.holiday.HolidayProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HolidayServiceTest {

    @InjectMocks
    private HolidayService holidayService;

    @Mock
    private HolidayProvider holidayProvider;

    @Mock
    private HolidayRepository holidayRepository;


    @Test
    public void shouldNotFetchAndSaveHolidaysIfTheyExistForGivenYear() {

        //Given
        int year = 2026;
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        when(holidayRepository.existsByDateBetween(startDate, endDate))
                .thenReturn(true);

        //When
        holidayService.fetchAndSaveHolidaysIfMissing(2026);

        //Then
        verify(holidayRepository).existsByDateBetween(startDate, endDate);
        verify(holidayProvider, never()).getHolidays(2026, "PL");
        verify(holidayRepository, never()).saveAll(anyList());
    }


    @Test
    public void shouldFetchAndSaveHolidaysIfTheyDoNotExistForGivenYear() {

        //Given
        int year = 2026;
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        List<NagerDateHolidayDto> holidayDtoList = List.of(
                new NagerDateHolidayDto(LocalDate.of(2026, 12,25), "Christmas"),
                new NagerDateHolidayDto(LocalDate.of(2026, 12,26), "Boxing Day")
        );

        when(holidayRepository.existsByDateBetween(startDate, endDate)).thenReturn(false);
        when(holidayProvider.getHolidays(2026, "PL")).thenReturn(holidayDtoList);

        //When
        holidayService.fetchAndSaveHolidaysIfMissing(2026);

        //Then
        ArgumentCaptor<List<Holiday>> captor = ArgumentCaptor.forClass(List.class);

        verify(holidayRepository).existsByDateBetween(startDate, endDate);
        verify(holidayProvider).getHolidays(2026, "PL");
        verify(holidayRepository).saveAll(captor.capture());

        List<Holiday> holidaysSaved = captor.getValue();

        assertThat(holidaysSaved).hasSize(2);

        assertThat(holidaysSaved.getFirst().getDate()).isEqualTo(LocalDate.of(2026, 12,25));
        assertThat(holidaysSaved.getFirst().getName()).isEqualTo("Christmas");

        assertThat(holidaysSaved.getLast().getDate()).isEqualTo(LocalDate.of(2026, 12,26));
        assertThat(holidaysSaved.getLast().getName()).isEqualTo("Boxing Day");
    }


    @Test
    void shouldDeleteOldHolidays() {

        //Given
        LocalDate date =  Year.now().atDay(1);

        //When
        holidayService.deleteOldHolidays();

        //Then
        verify(holidayRepository).deleteByDateBefore(date);
    }


    @Test
    void shouldReturnTrueWhenDateIsHoliday() {
        //Given
        LocalDate date =  Year.now().atDay(1);

        when(holidayRepository.existsByDate(date)).thenReturn(true);

        //When
        boolean result = holidayService.isHoliday(date);

        //Then
        assertThat(result).isTrue();
        verify(holidayRepository).existsByDate(date);
    }


    @Test
    void shouldReturnFalseWhenDateIsNotHoliday() {
        //Given
        LocalDate date =  Year.now().atDay(2);

        when(holidayRepository.existsByDate(date)).thenReturn(false);

        //When
        boolean result = holidayService.isHoliday(date);

        //Then
        assertThat(result).isFalse();
        verify(holidayRepository).existsByDate(date);
    }
}
