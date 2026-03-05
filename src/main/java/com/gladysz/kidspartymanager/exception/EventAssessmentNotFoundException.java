package com.gladysz.kidspartymanager.exception;

public class EventAssessmentNotFoundException extends RuntimeException {

    public EventAssessmentNotFoundException(Long id) {

        super("Event with id " + id + " has no assessment.");
    }

}
