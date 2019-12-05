package com.gpsolinski.remitnow.services;

import com.gpsolinski.remitnow.domain.Account;
import com.gpsolinski.remitnow.domain.Transaction;
import com.gpsolinski.remitnow.exceptions.InsufficientFundsException;
import com.gpsolinski.remitnow.exceptions.TransferFailedException;

import java.math.BigDecimal;

public interface TransferService {
    Transaction transferFunds(Account sender, Account recipient, BigDecimal amount) throws InsufficientFundsException;
}
