package com.gladysz.kidspartymanager.exception.orderer;

public class OrdererDeleteException extends RuntimeException {

    public OrdererDeleteException(Long id) {

        super("Orderer with id " + id +
                " cannot be deleted because it is assigned to existing reservations.");
    }
}
