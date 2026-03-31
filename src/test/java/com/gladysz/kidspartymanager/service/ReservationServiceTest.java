package com.gladysz.kidspartymanager.service;

import com.gladysz.kidspartymanager.domain.*;
import com.gladysz.kidspartymanager.exception.reservation.ReservationFilteringException;
import com.gladysz.kidspartymanager.mapper.ReservationMapper;
import com.gladysz.kidspartymanager.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private ReservationRepository reservationRepository;


    @Test
    void shouldReturnAllReservationsWhenNoFiltersProvided() {

        //Given
        Reservation r1 = new Reservation();
        r1.setStatus(Status.COMPLETED);
        r1.setEventDateTime(LocalDateTime.of(2026, 1, 10, 12, 0,0));

        Reservation r2 = new Reservation();
        r2.setStatus(Status.CANCELLED);
        r2.setEventDateTime(LocalDateTime.of(2026, 2, 16, 15, 0,0));

        Reservation r3 = new Reservation();
        r3.setStatus(Status.COMPLETED);
        r3.setEventDateTime(LocalDateTime.of(2026, 3, 20, 17, 0,0));

        when(reservationRepository.findAll()).thenReturn(List.of(r1, r2, r3));

        //When
        List<Reservation> reservations = reservationService.getReservations(null, null, null);

        //Then
        assertEquals(3, reservations.size());
        assertTrue(reservations.containsAll(List.of(r1, r2, r3)));
        verify(reservationRepository).findAll();
    }


    @Test
    void shouldReturnReservationsByStatusWhenOnlyStatusFilterProvided() {

        //Given
        Reservation r1 = new Reservation();
        r1.setStatus(Status.COMPLETED);
        r1.setEventDateTime(LocalDateTime.of(2026, 1, 10, 12, 0,0));

        Reservation r3 = new Reservation();
        r3.setStatus(Status.COMPLETED);
        r3.setEventDateTime(LocalDateTime.of(2026, 3, 20, 17, 0,0));

        when(reservationRepository.findByStatus(Status.COMPLETED)).thenReturn(List.of(r1, r3));

        //When
        List<Reservation> reservations = reservationService.getReservations(Status.COMPLETED, null, null);

        //Then
        assertEquals(2, reservations.size());
        assertTrue(reservations.containsAll(List.of(r1, r3)));
        verify(reservationRepository).findByStatus(Status.COMPLETED);
    }


    @Test
    void shouldThrowExceptionWhenFromDateIsAfterToDate() {

        //Given
        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = LocalDate.now().minusDays(5);

        //When & Then
        assertThrows(ReservationFilteringException.class,
                () ->  reservationService.getReservations(null, fromDate, toDate));
    }


    @Test
    void shouldReturnReservationsBetweenDatesWhenOnlyDatesProvided() {
        //Given
        LocalDate fromDate = LocalDate.of(2026, 2, 16);
        LocalDate toDate = LocalDate.of(2026,3,20);

        Reservation r2 = new Reservation();
        r2.setStatus(Status.CANCELLED);
        r2.setEventDateTime(LocalDateTime.of(2026, 2, 16, 15, 0,0));

        Reservation r3 = new Reservation();
        r3.setStatus(Status.COMPLETED);
        r3.setEventDateTime(LocalDateTime.of(2026, 3, 20, 17, 0,0));

        when(reservationRepository.findByEventDateTimeBetween(
                fromDate.atStartOfDay(), toDate.plusDays(1).atStartOfDay()))
                .thenReturn(List.of(r2, r3));

        //When
        List<Reservation> reservations = reservationService.getReservations(null, fromDate, toDate);

        //Then
        assertEquals(2, reservations.size());
        assertTrue(reservations.containsAll(List.of(r2, r3)));
        verify(reservationRepository).findByEventDateTimeBetween(
                fromDate.atStartOfDay(), toDate.plusDays(1).atStartOfDay());
    }


    @Test
    public void shouldReturnReservationsByStatusAndDatesWhenAllFiltersProvided() {

        //Given
        LocalDate fromDate = LocalDate.of(2026, 1, 10);
        LocalDate toDate = LocalDate.of(2026,3,20);

        Reservation r1 = new Reservation();
        r1.setStatus(Status.COMPLETED);
        r1.setEventDateTime(LocalDateTime.of(2026, 1, 10, 12, 0,0));

        Reservation r2 = new Reservation();
        r2.setStatus(Status.CANCELLED);
        r2.setEventDateTime(LocalDateTime.of(2026, 2, 16, 15, 0,0));

        Reservation r3 = new Reservation();
        r3.setStatus(Status.COMPLETED);
        r3.setEventDateTime(LocalDateTime.of(2026, 3, 20, 17, 0,0));

        when(reservationRepository.findByStatusAndEventDateTimeBetween(
                Status.COMPLETED, fromDate.atStartOfDay(), toDate.plusDays(1).atStartOfDay()))
                .thenReturn(List.of(r1, r3));

        //When
        List<Reservation> reservations = reservationService.getReservations(Status.COMPLETED, fromDate, toDate);

        //Then
        assertEquals(2, reservations.size());
        assertTrue(reservations.containsAll(List.of(r1, r3)));
        verify(reservationRepository).findByStatusAndEventDateTimeBetween(
                Status.COMPLETED, fromDate.atStartOfDay(), toDate.plusDays(1).atStartOfDay());
    }
}