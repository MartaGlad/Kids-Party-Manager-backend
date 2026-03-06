package com.gladysz.kidspartymanager.exception;

public class EventAssessmentNotFoundException extends RuntimeException {

    public EventAssessmentNotFoundException(Long reservationId) {

        super("EventAssessment for reservation with id " + reservationId + " not found.");
    }

}
