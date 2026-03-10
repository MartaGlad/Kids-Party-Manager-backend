package com.gladysz.kidspartymanager.exception.eventassessment;

public class EventAlreadyAssessedException extends RuntimeException {

    public EventAlreadyAssessedException(Long id) {

        super("Event with id " + id + " already assessed.");
    }
}
