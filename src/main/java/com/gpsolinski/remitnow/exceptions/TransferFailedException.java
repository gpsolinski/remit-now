package com.gpsolinski.remitnow.exceptions;

public class TransferFailedException extends Exception {

    public TransferFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
