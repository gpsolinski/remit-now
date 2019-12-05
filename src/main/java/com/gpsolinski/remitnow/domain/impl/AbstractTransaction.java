package com.gpsolinski.remitnow.domain.impl;

import com.gpsolinski.remitnow.domain.Transaction;
import com.gpsolinski.remitnow.domain.TransactionState;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;

@Getter
public abstract class AbstractTransaction implements Transaction {
    protected final Long id;
    protected final BigDecimal amount;
    protected AtomicReference<TransactionState> state;

    public AbstractTransaction(Long id, BigDecimal amount) {
        this.id = id;
        this.amount = amount;
        this.state = new AtomicReference<>(TransactionState.NEW);
    }

    @Override
    public TransactionState getState() {
        return state.get();
    }

    protected void validateIncompleteState() {
        if (isCompleted()) {
            throw new IllegalStateException("Transaction has already completed");
        }
    }

    private boolean isCompleted() {
        return state.get() == TransactionState.COMPLETED;
    }
}
