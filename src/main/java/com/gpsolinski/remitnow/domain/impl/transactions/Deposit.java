package com.gpsolinski.remitnow.domain.impl.transactions;

import com.gpsolinski.remitnow.domain.Account;
import com.gpsolinski.remitnow.domain.Transaction;
import com.gpsolinski.remitnow.domain.TransactionState;
import com.gpsolinski.remitnow.domain.impl.AbstractTransaction;
import com.gpsolinski.remitnow.domain.impl.TransactionType;
import lombok.Getter;

import java.math.BigDecimal;
/**
 * A transaction type representing a deposit.
 * It performs a debit operation on an account, but has no account to credit.
 *
 * @author Grzegorz Solinski
 */
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
    public TransactionType getType() {
        return TransactionType.DEPOSIT;
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
