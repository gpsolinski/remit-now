package domain.impl;

import domain.Account;
import domain.Transaction;
import domain.TransactionState;
import exceptions.InsufficientFundsException;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;

public final class Transfer implements Transaction {
    private final Long id;
    private final Account creditAccount;
    private final Account debitAccount;
    private final BigDecimal amount;
    private AtomicReference<TransactionState> state;

    public Transfer(Long id, Account creditAccount, Account debitAccount, BigDecimal amount) {
        this.id = id;
        this.creditAccount = creditAccount;
        this.debitAccount = debitAccount;
        this.amount = amount;
        this.state = new AtomicReference<>(TransactionState.NEW);
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public Account getCreditAccount() {
        return creditAccount;
    }

    @Override
    public Account getDebitAccount() {
        return debitAccount;
    }

    @Override
    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public TransactionState getState() {
        return state.get();
    }

    @Override
    public synchronized void complete() throws InsufficientFundsException {
        validateState();
        try {
            performTransfer();
        } catch (InsufficientFundsException e) {
            state.set(TransactionState.FAILED);
            throw e;
        }
        state.set(TransactionState.COMPLETED);
    }

    private void validateState() {
        if (isCompleted()) {
            throw new IllegalStateException("Transaction has already completed");
        }
    }

    private boolean isCompleted() {
        return state.get() == TransactionState.COMPLETED;
    }

    private void performTransfer() throws InsufficientFundsException {
        creditAccount.credit(amount);
        debitAccount.debit(amount);
    }
}
