package com.gladysz.kidspartymanager.exception;

public class ReservationOverlapException extends RuntimeException {

    public ReservationOverlapException() {

        super("This reservation time conflicts with already scheduled event.");
    }
}
