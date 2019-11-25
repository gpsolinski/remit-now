package domain.impl;

import com.google.common.annotations.VisibleForTesting;
import domain.BankAccount;
import exceptions.InsufficientFundsException;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class JointBankAccount implements BankAccount<Long> {
    private final Long id;
    private AtomicReference<BigDecimal> balance;
    private final Currency currency;

    private static final AtomicLong idCounter = new AtomicLong();

    public JointBankAccount() {
        this(Currency.getInstance(Locale.US));
    }

    public JointBankAccount(Currency currency) {
        this(currency, BigDecimal.ZERO);
    }

    @VisibleForTesting
    JointBankAccount(Currency currency, BigDecimal initialBalance) {
        id = idCounter.getAndIncrement();
        balance = new AtomicReference<>(initialBalance);
        this.currency = currency;
    }

    @Override
    public Long getID() {
        return id;
    }

    @Override
    public BigDecimal getBalance() {
        return balance.get();
    }

    @Override
    public Currency getCurrency() {
        return currency;
    }

    @Override
    public void deposit(BigDecimal amount) {
        balance.accumulateAndGet(amount, BigDecimal::add);
    }

    @Override
    public synchronized void withdraw(BigDecimal amount) throws InsufficientFundsException {
        BigDecimal witness = balance.get();
        if (witness.compareTo(amount) < 0) {
            throw new InsufficientFundsException();
        }
        balance.compareAndSet(witness, witness.subtract(amount));
    }
}
