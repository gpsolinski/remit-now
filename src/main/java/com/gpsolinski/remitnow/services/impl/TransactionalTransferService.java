package com.gpsolinski.remitnow.services.impl;

import com.gpsolinski.remitnow.domain.Account;
import com.gpsolinski.remitnow.domain.Transaction;
import com.gpsolinski.remitnow.domain.impl.transactions.Transfer;
import com.gpsolinski.remitnow.exceptions.InsufficientFundsException;
import com.gpsolinski.remitnow.exceptions.TransferFailedException;
import com.gpsolinski.remitnow.repository.AccountRepository;
import com.gpsolinski.remitnow.repository.TransactionRepository;
import com.gpsolinski.remitnow.services.TransferService;

import java.math.BigDecimal;

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
        transfer.complete();
        transactionRepository.save(transfer);
        accountRepository.save(sender);
        accountRepository.save(recipient);
        return transfer;
    }
}