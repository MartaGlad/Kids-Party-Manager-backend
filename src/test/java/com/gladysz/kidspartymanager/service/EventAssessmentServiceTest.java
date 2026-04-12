package com.gladysz.kidspartymanager.service;

import com.gladysz.kidspartymanager.domain.EventAssessment;
import com.gladysz.kidspartymanager.domain.Reservation;
import com.gladysz.kidspartymanager.domain.Status;

import com.gladysz.kidspartymanager.dto.eventassessment.EventAssessmentCreateDto;
import com.gladysz.kidspartymanager.exception.eventassessment.EventAlreadyAssessedException;
import com.gladysz.kidspartymanager.exception.eventassessment.EventAssessmentNotAllowedException;
import com.gladysz.kidspartymanager.exception.eventassessment.EventAssessmentNotFoundException;
import com.gladysz.kidspartymanager.repository.EventAssessmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventAssessmentServiceTest {

    @InjectMocks
    private EventAssessmentService eventAssessmentService;

    @Mock
    private EventAssessmentRepository eventAssessmentRepository;

    @Mock
    private ReservationService reservationService;


    @Test
    void shouldCreateEventAssessmentSuccessfully() {

        //Given
        EventAssessmentCreateDto dto = new EventAssessmentCreateDto(4, "Fine");

        Reservation reservation = new Reservation();
        reservation.setEventAssessment(null);
        reservation.setStatus(Status.COMPLETED);

        EventAssessment assessment = new EventAssessment();
        assessment.setRating(dto.rating());
        assessment.setComment(dto.comment());

        when(reservationService.getReservationById(1L)).thenReturn(reservation);
        when(eventAssessmentRepository.save(any(EventAssessment.class))).thenReturn(assessment);

        //When
        EventAssessment assessmentCreated = eventAssessmentService.createNewEventAssessment(1L, dto);

        //Then
        assertEquals(4, assessmentCreated.getRating());
        assertEquals("Fine", assessmentCreated.getComment());
        assertNotNull(reservation.getEventAssessment());
        assertEquals(4, reservation.getEventAssessment().getRating());
        assertEquals("Fine", reservation.getEventAssessment().getComment());
        verify(eventAssessmentRepository).save(any(EventAssessment.class));
    }


    @Test
    void shouldThrowExceptionWhenReservationIsAlreadyAssessedDuringCreatingNewEventAssessment() {

        //Given
        EventAssessmentCreateDto dto = new EventAssessmentCreateDto(4, "Fine");
        Reservation reservation = new Reservation();
        reservation.setEventAssessment(new EventAssessment());

        when(reservationService.getReservationById(1L)).thenReturn(reservation);

        //When & Then
        assertThrows(EventAlreadyAssessedException.class,
                () -> eventAssessmentService.createNewEventAssessment(1L, dto));
        verifyNoInteractions(eventAssessmentRepository);
    }


    @Test
    void shouldThrowExceptionWhenReservationIsNotCompletedDuringCreatingNewEventAssessment() {

        //Given
        EventAssessmentCreateDto dto = new EventAssessmentCreateDto(4, "Fine");
        Reservation reservation = new Reservation();
        reservation.setEventAssessment(null);
        reservation.setStatus(Status.CONFIRMED);

        when(reservationService.getReservationById(1L)).thenReturn(reservation);

        //When & Then
        assertThrows(EventAssessmentNotAllowedException.class,
                () -> eventAssessmentService.createNewEventAssessment(1L, dto));
        verifyNoInteractions(eventAssessmentRepository);
    }


    @Test
    void getByReservationIdShouldThrowExceptionWhenReservationHasNoAssessment() {

        //Given
        Reservation reservation = new Reservation();
        reservation.setEventAssessment(null);

        when(reservationService.getReservationById(1L)).thenReturn(reservation);

        //When & Then
        assertThrows(EventAssessmentNotFoundException.class,
                () -> eventAssessmentService.getByReservationId(1L));
    }


    @Test
    void getByReservationIdShouldReturnAssessmentWhenReservationHasAssessment() {

        //Given
        Reservation reservation = new Reservation();
        EventAssessment eventAssessment = new EventAssessment();
        eventAssessment.setRating(4);
        eventAssessment.setComment("Fine");
        reservation.setEventAssessment(eventAssessment);

        when(reservationService.getReservationById(1L)).thenReturn(reservation);

        //When
        EventAssessment assessmentFetched = eventAssessmentService.getByReservationId(1L);

        //Then
        assertSame(eventAssessment, assessmentFetched);
        assertEquals(4, assessmentFetched.getRating());
        assertEquals("Fine", assessmentFetched.getComment());
    }


    @Test
    void shouldDeleteEventAssessmentByReservationIdSuccessfully() {

        //Given
        Reservation reservation = new Reservation();
        EventAssessment eventAssessment = new EventAssessment();
        reservation.setEventAssessment(eventAssessment);

        when(reservationService.getReservationById(1L)).thenReturn(reservation);

        //When
        eventAssessmentService.deleteByReservationId(1L);

        //Then
        verify(eventAssessmentRepository).delete(eventAssessment);
    }
}
