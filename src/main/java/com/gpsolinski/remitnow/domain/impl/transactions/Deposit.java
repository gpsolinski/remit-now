package com.gpsolinski.remitnow.domain.impl.transactions;

import com.gpsolinski.remitnow.domain.Account;
import com.gpsolinski.remitnow.domain.Transaction;
import com.gpsolinski.remitnow.domain.TransactionState;
import com.gpsolinski.remitnow.domain.impl.AbstractTransaction;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class Deposit extends AbstractTransaction implements Transaction {

    private final Account debitAccount;

    public Deposit(Long id, Account debitAccount, BigDecimal amount) {
        super(id, amount);
        this.debitAccount = debitAccount;
    }

    @Override
    public Account getCreditAccount() {
        throw new UnsupportedOperationException("Deposit transaction does not have a credit account, only a debit account");
    }

    @Override
    public TransactionState getState() {
        return state.get();
    }

    @Override
    public synchronized void complete() {
        validateIncompleteState();
        try {
            debitAccount.debit(amount);
        } catch (Exception e) {
            state.set(TransactionState.FAILED);
            throw e;
        }
        state.set(TransactionState.COMPLETED);
    }
}
