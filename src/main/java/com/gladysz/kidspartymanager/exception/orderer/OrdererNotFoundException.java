package com.gladysz.kidspartymanager.exception.orderer;

public class OrdererNotFoundException extends RuntimeException {

    public OrdererNotFoundException(Long id) {

        super("Orderer with id " + id + " not found.");
    }
}
