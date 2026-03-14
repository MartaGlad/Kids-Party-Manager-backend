package com.gladysz.kidspartymanager.exception.eventpackage;

public class EventPackageDeleteException extends RuntimeException {

    public EventPackageDeleteException(Long id) {
        super("Event package with id " + id +
                " cannot be deleted because it is assigned to existing reservations.");
    }
}
