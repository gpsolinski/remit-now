package com.gpsolinski.remitnow.domain.impl.transactions;

import com.gpsolinski.remitnow.domain.Account;
import com.gpsolinski.remitnow.domain.Transaction;
import com.gpsolinski.remitnow.domain.TransactionState;
import com.gpsolinski.remitnow.domain.impl.AbstractTransaction;
import com.gpsolinski.remitnow.exceptions.InsufficientFundsException;
import lombok.Getter;

import java.math.BigDecimal;
/**
 * A transaction type representing a withdrawal.
 * It performs a credit operation on an account, but has no account to debit.
 *
 * @author Grzegorz Solinski
 */
@Getter
public class Withdrawal extends AbstractTransaction implements Transaction {

    private final Account creditAccount;

    public Withdrawal(Long id, Account creditAccount, BigDecimal amount) {
        super(id, amount);
        this.creditAccount = creditAccount;
    }

    @Override
    public Account getDebitAccount() {
        throw new UnsupportedOperationException("Withdrawal transaction does not have a debit account, only a credit account");
    }

    @Override
    public synchronized void complete() throws InsufficientFundsException {
        validateIncompleteState();
        try {
            creditAccount.credit(amount);
        } catch (Exception e) {
            state.set(TransactionState.FAILED);
            throw e;
        }
        state.set(TransactionState.COMPLETED);
    }
}
