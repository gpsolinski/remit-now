/*
 * Copyright (c) 2019 Grzegorz Solinski. All rights reserved.
 */

package com.gpsolinski.remitnow.repository.impl;

import com.gpsolinski.remitnow.domain.Account;
import com.gpsolinski.remitnow.domain.Transaction;
import com.gpsolinski.remitnow.domain.impl.transactions.Deposit;
import com.gpsolinski.remitnow.domain.impl.transactions.Transfer;
import com.gpsolinski.remitnow.domain.impl.transactions.Withdrawal;
import com.gpsolinski.remitnow.repository.TransactionRepository;
import lombok.val;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicLong;

public class DefaultTransactionRepository extends BasicInMemoryRepository<Transaction, Long> implements TransactionRepository  {
    private final AtomicLong idCounter = new AtomicLong();

    @Override
    public Deposit createDeposit(Account account, BigDecimal amount) {
        val deposit = new Deposit(idCounter.incrementAndGet(), account, amount);
        super.save(deposit);
        return deposit;
    }

    @Override
    public Withdrawal createWithdrawal(Account account, BigDecimal amount) {
        val withdrawal = new Withdrawal(idCounter.incrementAndGet(), account, amount);
        super.save(withdrawal);
        return withdrawal;
    }

    @Override
    public Transfer createTransfer(Account sender, Account recipient, BigDecimal amount) {
        val transfer = new Transfer(idCounter.incrementAndGet(), sender, recipient, amount);
        super.save(transfer);
        return transfer;
    }
}
