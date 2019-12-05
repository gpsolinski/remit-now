/*
 * Copyright (c) 2019 Grzegorz Solinski. All rights reserved.
 */

package com.gpsolinski.remitnow.services;

import com.gpsolinski.remitnow.domain.Account;
import com.gpsolinski.remitnow.domain.Transaction;
import com.gpsolinski.remitnow.exceptions.InsufficientFundsException;

import java.math.BigDecimal;

public interface TransferService {
    Transaction transferFunds(Account sender, Account recipient, BigDecimal amount) throws InsufficientFundsException;
}
