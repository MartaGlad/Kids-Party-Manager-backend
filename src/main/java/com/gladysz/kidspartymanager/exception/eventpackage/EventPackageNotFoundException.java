package com.gladysz.kidspartymanager.exception.eventpackage;

public class EventPackageNotFoundException extends RuntimeException {

    public EventPackageNotFoundException(Long id) {

        super("Event package with id " + id + " not found.");
    }
}

