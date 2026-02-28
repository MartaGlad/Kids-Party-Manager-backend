package com.gladysz.kidspartymanager.exception;

public class EventPackageNotFoundException extends RuntimeException {

    public EventPackageNotFoundException(Long id) {

        super("EventPackage with id " + id + " not found.");
    }
}

