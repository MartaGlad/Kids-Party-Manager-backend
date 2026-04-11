package com.gladysz.kidspartymanager.service;

import com.gladysz.kidspartymanager.domain.*;

import com.gladysz.kidspartymanager.dto.eventassessment.EventAssessmentCreateDto;
import com.gladysz.kidspartymanager.exception.eventassessment.EventAlreadyAssessedException;
import com.gladysz.kidspartymanager.exception.eventassessment.EventAssessmentNotAllowedException;
import com.gladysz.kidspartymanager.exception.eventassessment.EventAssessmentNotFoundException;
import com.gladysz.kidspartymanager.repository.EventAssessmentRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;



@Service
@Transactional
@RequiredArgsConstructor
public class EventAssessmentService {

    private final EventAssessmentRepository eventAssessmentRepository;
    private final ReservationService reservationService;


    public EventAssessment createNewEventAssessment(
            final Long reservationId,
            final EventAssessmentCreateDto eventAssessmentCreateDto) {

        Reservation reservation = reservationService.getReservationById(reservationId);

        if (reservation.hasAssessment()) {
            throw new EventAlreadyAssessedException(reservationId);
        }

        if (!reservation.isCompleted()) {
            throw new EventAssessmentNotAllowedException(reservationId);
        }

        EventAssessment eventAssessment = new EventAssessment();
        eventAssessment.setRating(eventAssessmentCreateDto.rating());
        eventAssessment.setComment(eventAssessmentCreateDto.comment());

        reservation.setEventAssessment(eventAssessment);

        return eventAssessmentRepository.save(eventAssessment);
    }


    @Transactional(readOnly = true)
    public EventAssessment getByReservationId(final Long reservationId) {

        Reservation reservation = reservationService.getReservationById(reservationId);

        EventAssessment assessment = reservation.getEventAssessment();
        if (assessment == null) {
            throw new EventAssessmentNotFoundException(reservationId);
        }
        return assessment;
    }


    public void deleteByReservationId(final Long reservationId) {

        EventAssessment fetchedAssessment = getByReservationId(reservationId);

        eventAssessmentRepository.delete(fetchedAssessment);
    }
}