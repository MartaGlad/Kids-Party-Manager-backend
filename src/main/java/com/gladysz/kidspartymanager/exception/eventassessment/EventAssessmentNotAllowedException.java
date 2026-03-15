package com.gladysz.kidspartymanager.exception.eventassessment;

public class EventAssessmentNotAllowedException extends RuntimeException {

    public EventAssessmentNotAllowedException(Long reservationId) {

        super("Reservation with id " + reservationId +
                " cannot be assessed because it is not completed.");
    }
}
