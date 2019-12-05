/*
 * Copyright (c) 2019 Grzegorz Solinski. All rights reserved.
 */

package com.gpsolinski.remitnow.context;

import com.gpsolinski.remitnow.repository.AccountRepository;
import com.gpsolinski.remitnow.repository.TransactionRepository;
import com.gpsolinski.remitnow.repository.impl.DefaultAccountRepository;
import com.gpsolinski.remitnow.repository.impl.DefaultTransactionRepository;
import com.gpsolinski.remitnow.services.TransactionService;
import com.gpsolinski.remitnow.services.impl.DefaultTransactionService;
import lombok.Getter;
import lombok.val;

@Getter
public class ApplicationContext {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionService transactionService;

    private ApplicationContext(AccountRepository accountRepository,
                               TransactionRepository transactionRepository,
                               TransactionService transactionService) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.transactionService = transactionService;
    }

    public static ApplicationContext create() {
        val accountsRepository = new DefaultAccountRepository();
        val transactionRepository = new DefaultTransactionRepository();
        val transferService = new DefaultTransactionService(accountsRepository, transactionRepository);
        return new ApplicationContext(accountsRepository, transactionRepository, transferService);
    }
}
