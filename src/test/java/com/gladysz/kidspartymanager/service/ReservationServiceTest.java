package com.gladysz.kidspartymanager.service;

import com.gladysz.kidspartymanager.domain.*;
import com.gladysz.kidspartymanager.domain.pricing.PricingResult;
import com.gladysz.kidspartymanager.dto.reservation.ReservationCreateDto;
import com.gladysz.kidspartymanager.dto.reservation.ReservationUpdateDto;
import com.gladysz.kidspartymanager.exception.animator.AnimatorInactiveException;
import com.gladysz.kidspartymanager.exception.reservation.*;
import com.gladysz.kidspartymanager.mapper.ReservationMapper;
import com.gladysz.kidspartymanager.repository.ReservationRepository;
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


    @Mock
    private ReservationMapper reservationMapper;



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


    @Test
    void shouldUpdateReservationSuccessfully() {

        //Given
        Reservation r1 = new Reservation();
        r1.setEventDateTime(LocalDateTime.of(2026, 1, 20, 12, 0,0));
        r1.setStatus(Status.NEW);
        r1.setBirthdayChildAge(1);

        ReservationUpdateDto dto = new ReservationUpdateDto(
                1L, LocalDateTime.of(2026, 1, 10, 12, 0,0),
                16, 3);

        EventPackage eventPackage = new EventPackage();
        eventPackage.setDurationHr(2);

        PricingResult pricingResult = new PricingResult(BigDecimal.valueOf(1000), BigDecimal.valueOf(233.06),
                BigDecimal.valueOf(269.01), BigDecimal.valueOf(203.41), false);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(r1));
        when(eventPackageService.getEventPackageById(1L)).thenReturn(eventPackage);

        doAnswer(i -> {
            Reservation reservation = i.getArgument(0);
            ReservationUpdateDto updateDto = i.getArgument(1);

            reservation.setEventDateTime(updateDto.eventDateTime());
            reservation.setChildrenCount(updateDto.childrenCount());
            reservation.setBirthdayChildAge(updateDto.birthdayChildAge());

            return null;
        }).when(reservationMapper).applyUpdate(r1, dto);

        when(pricingService.getPricingResult(eventPackage, dto.childrenCount(), dto.eventDateTime().toLocalDate()))
                .thenReturn(pricingResult);

        //When
        reservationService.updateReservation(1L, dto);

        //Then
        assertEquals(dto.eventDateTime(), r1.getEventDateTime());
        assertEquals(pricingResult.finalPricePln(), r1.getPriceSnapshot());
        assertEquals(3, r1.getBirthdayChildAge());
        verify(reservationMapper).applyUpdate(r1, dto);
        verify(pricingService).getPricingResult(eventPackage, dto.childrenCount(), dto.eventDateTime().toLocalDate());
    }


    @Test
    void shouldThrowExceptionWhenReservationCannotBeUpdated() {
        //Given
        Reservation r1 = new Reservation();
        r1.setStatus(Status.COMPLETED);

        ReservationUpdateDto dto = new ReservationUpdateDto(
                1L, LocalDateTime.of(2026, 1, 10, 12, 0,0),
                16, 3);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(r1));

        //When & Then
        assertThrows(ReservationUpdateException.class, () ->
                reservationService.updateReservation(1L, dto));
        verify(reservationRepository).findById(1L);
    }


    @Test
    void shouldThrowExceptionWhenUpdatedReservationOverlaps() {
        //Given
        ReservationUpdateDto dto = new ReservationUpdateDto(
                1L, LocalDateTime.of(2026, 1, 10, 12, 0,0),
                16, 3);

        EventPackage eventPackage = new EventPackage();
        eventPackage.setDurationHr(2);

        Reservation rUpdated = new Reservation();
        ReflectionTestUtils.setField(rUpdated, "id", 1L);
        rUpdated.setStatus(Status.NEW);
        rUpdated.setEventDateTime(LocalDateTime.of(2026, 1, 9, 12, 0,0));
        rUpdated.setEventPackage(eventPackage);

        Reservation rCollided = new Reservation();
        ReflectionTestUtils.setField(rCollided, "id", 5L);
        rCollided.setStatus(Status.NEW);
        rCollided.setEventDateTime(LocalDateTime.of(2026, 1, 10, 11, 0,0));
        rCollided.setEventPackage(eventPackage);

        LocalDateTime dayStart = LocalDate.of(2026, 1, 10).atStartOfDay();
        LocalDateTime nextDayStart = LocalDate.of(2026, 1, 10).plusDays(1).atStartOfDay();

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(rUpdated));
        when(eventPackageService.getEventPackageById(1L)).thenReturn(eventPackage);
        when(reservationRepository.findActiveReservationsForDay(
                dayStart, nextDayStart)).thenReturn(List.of(rCollided));

        //When & Then
        assertThrows(ReservationOverlapException.class, () -> reservationService.updateReservation(1L, dto));
    }


    @Test
    void shouldThrowExceptionWhenUpdatedReservationNotFound() {

        //Given
        ReservationUpdateDto dto = new ReservationUpdateDto(
                1L, LocalDateTime.of(2026, 1, 10, 12, 0,0),
                16, 3);
        when(reservationRepository.findById(1L)).thenReturn(Optional.empty());

        //When & Then
        assertThrows(ReservationNotFoundException.class, () -> reservationService.updateReservation(1L, dto));
        verify(reservationRepository).findById(1L);

    }

}