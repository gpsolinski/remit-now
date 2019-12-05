/*
 * Copyright (c) 2019 Grzegorz Solinski. All rights reserved.
 */

package com.gpsolinski.remitnow.context;

import com.gpsolinski.remitnow.repository.AccountRepository;
import com.gpsolinski.remitnow.repository.TransactionRepository;
import com.gpsolinski.remitnow.repository.impl.DefaultAccountRepository;
import com.gpsolinski.remitnow.repository.impl.DefaultTransactionRepository;
import com.gpsolinski.remitnow.services.TransferService;
import com.gpsolinski.remitnow.services.impl.TransactionalTransferService;
import lombok.Getter;
import lombok.val;

@Getter
public class ApplicationContext {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final TransferService transferService;

    private ApplicationContext(AccountRepository accountRepository,
                               TransactionRepository transactionRepository,
                               TransferService transferService) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.transferService = transferService;
    }

    public static ApplicationContext create() {
        val accountsRepository = new DefaultAccountRepository();
        val transactionRepository = new DefaultTransactionRepository();
        val transferService = new TransactionalTransferService(accountsRepository, transactionRepository);
        return new ApplicationContext(accountsRepository, transactionRepository, transferService);
    }
}
