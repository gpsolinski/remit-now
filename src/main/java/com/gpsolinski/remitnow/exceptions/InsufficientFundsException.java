package com.gpsolinski.remitnow.exceptions;

public class InsufficientFundsException extends Exception {
    public InsufficientFundsException(Long id) {
        super("Insufficient balance in the account with ID " + id);
    }
}
