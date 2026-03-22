package com.gladysz.kidspartymanager.exception.reservation;

public class ReservationTimeException extends RuntimeException {

    public ReservationTimeException() {
        super("Reservation time must be between 8 and 22 hours");
    }
}
