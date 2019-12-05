/*
 * Copyright (c) 2019 Grzegorz Solinski. All rights reserved.
 */

package com.gpsolinski.remitnow.services;

import com.gpsolinski.remitnow.domain.Account;
import com.gpsolinski.remitnow.domain.Transaction;
import com.gpsolinski.remitnow.domain.impl.transactions.Deposit;
import com.gpsolinski.remitnow.domain.impl.transactions.Withdrawal;
import com.gpsolinski.remitnow.exceptions.InsufficientFundsException;

import java.math.BigDecimal;

public interface TransactionService {
    Deposit depositFunds(Account account, BigDecimal amount);
    Withdrawal withdrawFunds(Account account, BigDecimal amount) throws InsufficientFundsException;
    Transaction transferFunds(Account sender, Account recipient, BigDecimal amount) throws InsufficientFundsException;
}
