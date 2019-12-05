/*
 * Copyright (c) 2019 Grzegorz Solinski. All rights reserved.
 */

package com.gpsolinski.remitnow.services.impl;

import com.gpsolinski.remitnow.domain.Account;
import com.gpsolinski.remitnow.domain.Transaction;
import com.gpsolinski.remitnow.domain.impl.transactions.Transfer;
import com.gpsolinski.remitnow.exceptions.InsufficientFundsException;
import com.gpsolinski.remitnow.repository.AccountRepository;
import com.gpsolinski.remitnow.repository.TransactionRepository;
import com.gpsolinski.remitnow.services.TransferService;

import java.math.BigDecimal;
/**
 * The default implementation of the transfer service.
 * It provides a layer of abstraction over the operations performed on the accounts during a transfer.
 * It ensures that a transfer (even in a failed state) will be saved to the repository.
 *
 * @author Grzegorz Solinski
 */
public class TransactionalTransferService implements TransferService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransactionalTransferService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
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
