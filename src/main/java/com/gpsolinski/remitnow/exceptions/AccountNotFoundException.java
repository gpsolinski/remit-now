package com.gpsolinski.remitnow.exceptions;

public class AccountNotFoundException extends Exception {

    public AccountNotFoundException(Long accountId) {
        super("Account with ID " + accountId + " does not exist.");
    }
}
