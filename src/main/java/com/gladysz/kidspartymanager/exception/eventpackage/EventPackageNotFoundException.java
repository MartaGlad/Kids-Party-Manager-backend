package com.gladysz.kidspartymanager.exception.eventpackage;

public class EventPackageNotFoundException extends RuntimeException {

    public EventPackageNotFoundException(Long id) {

        super("EventPackage with id " + id + " not found.");
    }
}

