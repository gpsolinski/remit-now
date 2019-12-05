/*
 * Copyright (c) 2019 Grzegorz Solinski. All rights reserved.
 */

package com.gpsolinski.remitnow.domain.impl.accounts;

import com.gpsolinski.remitnow.domain.Account;
import com.gpsolinski.remitnow.exceptions.InsufficientFundsException;
import com.gpsolinski.remitnow.validators.TransactionAmountValidator;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.concurrent.atomic.AtomicReference;
/**
 * A default implementation of the Account interface, representing a debit account.
 * Debit account is an account, in which a debit operation increases balance,
 * while credit operation decreases balance.
 * The operations on the account are thread-safe.
 *
 * @author Grzegorz Solinski
 */
@Getter
public final class DebitAccount implements Account {

    private final Long id;
    private AtomicReference<BigDecimal> balance;
    private AtomicReference<BigDecimal> availableBalance;
    private final Currency currency;

    public DebitAccount(Long id, Currency currency, BigDecimal initialBalance) {
        this.id = id;
        balance = new AtomicReference<>(initialBalance);
        availableBalance = new AtomicReference<>(initialBalance);
        this.currency = currency;
    }

    @Override
    public BigDecimal getBalance() {
        return balance.get();
    }

    @Override
    public BigDecimal getAvailableBalance() {
        return availableBalance.get();
    }

    @Override
    public void debit(BigDecimal amount) {
        TransactionAmountValidator.validate(amount);
        availableBalance.accumulateAndGet(amount, BigDecimal::add);
    }

    @Override
    public synchronized void credit(BigDecimal amount) throws InsufficientFundsException {
        TransactionAmountValidator.validate(amount);
        BigDecimal currentBalance = availableBalance.get();
        if (currentBalance.compareTo(amount) < 0) {
            throw new InsufficientFundsException(id);
        }
        availableBalance.accumulateAndGet(amount, BigDecimal::subtract);
    }

    @Override
    public synchronized void updateBalance() {
        BigDecimal availableBalance = getAvailableBalance();
        balance.set(availableBalance);
    }
}
