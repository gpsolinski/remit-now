package com.gpsolinski.remitnow.domain.impl.transactions;

import com.gpsolinski.remitnow.domain.Account;
import com.gpsolinski.remitnow.domain.Transaction;
import com.gpsolinski.remitnow.domain.TransactionState;
import com.gpsolinski.remitnow.domain.impl.AbstractTransaction;
import com.gpsolinski.remitnow.domain.impl.TransactionType;
import com.gpsolinski.remitnow.exceptions.InsufficientFundsException;
import lombok.Getter;

import java.math.BigDecimal;
/**
 * A transaction type representing a transfer.
 * It performs a credit operation on one account, and a debit operation on another.
 * It does so in a thread-safe manner.
 *
 * @author Grzegorz Solinski
 */
@Getter
public final class Transfer extends AbstractTransaction implements Transaction {

    private final Account creditAccount;
    private final Account debitAccount;

    public Transfer(Long id, Account creditAccount, Account debitAccount, BigDecimal amount) {
        super(id, amount);
        this.creditAccount = creditAccount;
        this.debitAccount = debitAccount;
    }

    @Override
    public TransactionType getType() {
        return TransactionType.TRANSFER;
    }

    @Override
    public synchronized void complete() throws InsufficientFundsException {
        validateIncompleteState();
        try {
            performTransfer();
        } catch (Exception e) {
            state.set(TransactionState.FAILED);
            throw e;
        }
        state.set(TransactionState.COMPLETED);
    }

    private void performTransfer() throws InsufficientFundsException {
        creditAccount.credit(getAmount());
        debitAccount.debit(getAmount());
    }
}
