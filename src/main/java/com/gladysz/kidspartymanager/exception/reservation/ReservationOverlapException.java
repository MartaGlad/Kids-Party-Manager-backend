package com.gladysz.kidspartymanager.exception.reservation;

public class ReservationOverlapException extends RuntimeException {

    public ReservationOverlapException() {

        super("This reservation time conflicts with already scheduled event.");
    }
}
