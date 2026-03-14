package com.gladysz.kidspartymanager.exception.reservation;

public class ReservationUpdateException extends RuntimeException {

    public ReservationUpdateException() {

        super("Only reservations with status NEW and CONFIRMED can be updated.");
    }
}
