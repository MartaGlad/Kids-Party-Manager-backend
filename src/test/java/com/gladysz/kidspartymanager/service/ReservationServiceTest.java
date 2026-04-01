package com.gladysz.kidspartymanager.service;

import com.gladysz.kidspartymanager.domain.*;
import com.gladysz.kidspartymanager.domain.pricing.PricingResult;
import com.gladysz.kidspartymanager.dto.reservation.ReservationCreateDto;
import com.gladysz.kidspartymanager.exception.animator.AnimatorInactiveException;
import com.gladysz.kidspartymanager.exception.reservation.ReservationFilteringException;
import com.gladysz.kidspartymanager.exception.reservation.ReservationNotFoundException;
import com.gladysz.kidspartymanager.exception.reservation.ReservationOverlapException;
import com.gladysz.kidspartymanager.repository.EventPackageRepository;
import com.gladysz.kidspartymanager.repository.ReservationRepository;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private EventPackageService eventPackageService;

    @Mock
    private AnimatorService animatorService;

    @Mock
    private OrdererService ordererService;

    @Mock
    private PricingService pricingService;




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


    @Test
    void shouldReturnReservationWhenExists() {

        //Given
        Reservation r1 = new Reservation();
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(r1));

        //When
        Reservation r = reservationService.getReservationById(1L);

        //Then
        assertEquals(r1, r);
        verify(reservationRepository).findById(1L);
    }


    @Test
    void shouldThrowExceptionWhenReservationNotFound() {

        //Given
        when(reservationRepository.findById(1L)).thenReturn(Optional.empty());

        //When & Then
        assertThrows(ReservationNotFoundException.class, ()
            -> reservationService.getReservationById(1L));
        verify(reservationRepository).findById(1L);
    }


    @Test
    void shouldCreateReservationSuccessfully() {

        //Given
        ReservationCreateDto dto = new ReservationCreateDto(
                1L, 1L, 1L,
                LocalDateTime.of(2026, 1, 10, 12, 0,0),
                16, 2);

        EventPackage eventPackage = new EventPackage();
        eventPackage.setDurationHr(2);

        Animator animator = new Animator();
        animator.setActive(true);

        Orderer orderer = new Orderer();

        PricingResult pricingResult = new PricingResult(BigDecimal.valueOf(1000), BigDecimal.valueOf(233.06),
                BigDecimal.valueOf(269.01), BigDecimal.valueOf(203.41), false);

        when(eventPackageService.getEventPackageById(1L)).thenReturn(eventPackage);
        when(animatorService.getAnimatorById(1L)).thenReturn(animator);
        when(ordererService.getOrdererById(1L)).thenReturn(orderer);
        when(pricingService.getPricingResult(eventPackage, dto.childrenCount(), dto.eventDateTime().toLocalDate()))
                .thenReturn(pricingResult);
        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(i -> i.getArgument(0));

        //When
        Reservation r = reservationService.createNewReservation(dto);

        //Then
        assertEquals(eventPackage, r.getEventPackage());
        assertEquals(animator, r.getAnimator());
        assertEquals(orderer, r.getOrderer());
        assertEquals(dto.eventDateTime(), r.getEventDateTime());
        assertEquals(pricingResult.finalPricePln(), r.getPriceSnapshot());
        assertFalse(r.isHolidayFlag());
        verify(reservationRepository).save(any(Reservation.class));
    }


    @Test
    void shouldThrowExceptionWhenInactiveAnimator() {

        //Given
        ReservationCreateDto dto = new ReservationCreateDto(
                1L, 1L, 1L,
                LocalDateTime.of(2026, 1, 10, 12, 0,0),
                16, 2);

        EventPackage eventPackage = new EventPackage();
        eventPackage.setDurationHr(2);

        Animator animator = new Animator();
        animator.setActive(false);
        when(eventPackageService.getEventPackageById(1L)).thenReturn(eventPackage);
        when(animatorService.getAnimatorById(1L)).thenReturn(animator);

        //When & Then
        assertThrows(AnimatorInactiveException.class, () ->  reservationService.createNewReservation(dto));
        verify(reservationRepository, never()).save(any());
    }


    @Test
    void shouldThrowExceptionWhenReservationOverlaps() {

        //Given
        ReservationCreateDto dto = new ReservationCreateDto(
                1L, 1L, 1L,
                LocalDateTime.of(2026, 1, 10, 12, 0,0),
                16, 2);

        EventPackage eventPackage = new EventPackage();
        eventPackage.setDurationHr(2);

        Reservation r1 = new Reservation();
        ReflectionTestUtils.setField(r1, "id", 5L);
        r1.setStatus(Status.CONFIRMED);
        r1.setEventDateTime(LocalDateTime.of(2026, 1, 10, 12, 0,0));
        r1.setEventPackage(eventPackage);

        LocalDateTime dayStart = LocalDate.of(2026, 1, 10).atStartOfDay();
        LocalDateTime nextDayStart = LocalDate.of(2026, 1, 10).plusDays(1).atStartOfDay();

        when(eventPackageService.getEventPackageById(1L)).thenReturn(eventPackage);
        when(reservationRepository.findActiveReservationsForDay(dayStart, nextDayStart)).thenReturn(List.of(r1));

        //When & Then
        assertThrows(ReservationOverlapException.class, () -> reservationService.createNewReservation(dto));
        verify(reservationRepository, never()).save(any());
    }


    @Test
    void shouldChangeReservationStatusSuccessfully() {

        //Given
        Reservation r1 = new Reservation();
        r1.setStatus(Status.CONFIRMED);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(r1));

        //When
        Reservation r = reservationService.changeReservationStatusById(1L, Status.COMPLETED);

        //Then
        assertEquals(Status.COMPLETED, r.getStatus());
        verify(reservationRepository).findById(1L);
    }


    @Test
    void shouldThrowExceptionWhenChangingStatusForNonExistingReservation() {

        //Given
        when(reservationRepository.findById(1L)).thenReturn(Optional.empty());

        //When & Then
        assertThrows(ReservationNotFoundException.class,
                () -> reservationService.changeReservationStatusById(1L, Status.COMPLETED));

    }
}