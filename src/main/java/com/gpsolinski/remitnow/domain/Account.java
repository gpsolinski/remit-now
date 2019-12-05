package com.gpsolinski.remitnow.domain;

import com.gpsolinski.remitnow.exceptions.InsufficientFundsException;

import java.math.BigDecimal;
import java.util.Currency;

public interface Account extends Identifiable<Long> {
    BigDecimal getBalance();
    BigDecimal getAvailableBalance();
    Currency getCurrency();
    void debit(BigDecimal amount);
    void credit(BigDecimal amount) throws InsufficientFundsException;
    void updateBalance();
}
