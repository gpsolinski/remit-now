/*
 * Copyright (c) 2019 Grzegorz Solinski. All rights reserved.
 */

package com.gpsolinski.remitnow.exceptions;

public class AccountNotFoundException extends Exception {

    public AccountNotFoundException(Long accountId) {
        super("Account with ID " + accountId + " does not exist.");
    }
}
