package com.gladysz.kidspartymanager.exception;

import com.gladysz.kidspartymanager.domain.Status;

public class ReservationChangeStatusException extends RuntimeException {

    public ReservationChangeStatusException(Status currentStatus, Status newStatus) {

        super("Can't change " + currentStatus + " to " + newStatus + ".");
    }

}