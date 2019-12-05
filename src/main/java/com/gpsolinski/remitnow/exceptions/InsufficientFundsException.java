package com.gpsolinski.remitnow.exceptions;

public class InsufficientFundsException extends Exception {
    public InsufficientFundsException() {
        super("Insufficient balance in the given account");
    }
}
