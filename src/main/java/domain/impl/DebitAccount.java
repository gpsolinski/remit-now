package domain.impl;

import domain.Account;
import exceptions.InsufficientFundsException;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.concurrent.atomic.AtomicReference;

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
    public Long getId() {
        return id;
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
    public Currency getCurrency() {
        return currency;
    }

    @Override
    public void debit(BigDecimal amount) {
        availableBalance.accumulateAndGet(amount, BigDecimal::add);
    }

    @Override
    public synchronized void credit(BigDecimal amount) throws InsufficientFundsException {
        BigDecimal currentBalance = availableBalance.get();
        if (currentBalance.compareTo(amount) < 0) {
            throw new InsufficientFundsException();
        }
        availableBalance.accumulateAndGet(amount, BigDecimal::subtract);
    }

    @Override
    public synchronized void updateBalance() {
        BigDecimal availableBalance = getAvailableBalance();
        balance.set(availableBalance);
    }
}
