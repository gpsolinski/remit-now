/*
 * Copyright (c) 2019 Grzegorz Solinski. All rights reserved.
 */

package com.gpsolinski.remitnow.services.impl;

import com.gpsolinski.remitnow.domain.Account;
import com.gpsolinski.remitnow.domain.Transaction;
import com.gpsolinski.remitnow.domain.impl.transactions.Deposit;
import com.gpsolinski.remitnow.domain.impl.transactions.Transfer;
import com.gpsolinski.remitnow.domain.impl.transactions.Withdrawal;
import com.gpsolinski.remitnow.exceptions.InsufficientFundsException;
import com.gpsolinski.remitnow.repository.AccountRepository;
import com.gpsolinski.remitnow.repository.TransactionRepository;
import com.gpsolinski.remitnow.services.TransactionService;

import java.math.BigDecimal;
/**
 * The default implementation of the transfer service.
 * It provides a layer of abstraction over the operations performed on the accounts during transactions.
 * It ensures that a transaction (even in a failed state) will be saved to the repository,
 * and that a successful transaction is followed by updating the accounts.
 *
 * @author Grzegorz Solinski
 */
public class DefaultTransactionService implements TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public DefaultTransactionService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Deposit depositFunds(Account account, BigDecimal amount) {
        final Deposit deposit = transactionRepository.createDeposit(account, amount);
        try {
            deposit.complete();

            if (deposit.isCompleted()) {
                account.updateBalance();
                accountRepository.save(account);
            }
        } finally {
            transactionRepository.save(deposit);
        }
        return deposit;
    }

    @Override
    public Withdrawal withdrawFunds(Account account, BigDecimal amount) throws InsufficientFundsException {
        final Withdrawal withdrawal = transactionRepository.createWithdrawal(account, amount);
        try {
            withdrawal.complete();

            if (withdrawal.isCompleted()) {
                account.updateBalance();
                accountRepository.save(account);
            }
        } finally {
            transactionRepository.save(withdrawal);
        }
        return withdrawal;
    }

    @Override
    public Transaction transferFunds(Account sender, Account recipient, BigDecimal amount) throws InsufficientFundsException {
        final Transfer transfer = transactionRepository.createTransfer(sender, recipient, amount);
        try {
            transfer.complete();

            if (transfer.isCompleted()) {
                sender.updateBalance();
                recipient.updateBalance();
                accountRepository.save(sender);
                accountRepository.save(recipient);
            }
        } finally {
            transactionRepository.save(transfer);
        }
        return transfer;
    }
}
