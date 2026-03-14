package com.gladysz.kidspartymanager.exception.eventassessment;

public class EventAssessmentNotFoundException extends RuntimeException {

    public EventAssessmentNotFoundException(Long reservationId) {

        super("Event assessment for reservation with id " + reservationId + " not found.");
    }

}
