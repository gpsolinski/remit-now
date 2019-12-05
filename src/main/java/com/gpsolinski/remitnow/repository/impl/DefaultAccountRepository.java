/*
 * Copyright (c) 2019 Grzegorz Solinski. All rights reserved.
 */

package com.gpsolinski.remitnow.repository.impl;

import com.gpsolinski.remitnow.domain.Account;
import com.gpsolinski.remitnow.domain.impl.accounts.DebitAccount;
import com.gpsolinski.remitnow.repository.AccountRepository;
import lombok.val;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.concurrent.atomic.AtomicLong;

public class DefaultAccountRepository extends BasicInMemoryRepository<Account, Long> implements AccountRepository {
    private static final AtomicLong idCounter = new AtomicLong();

    @Override
    public DebitAccount createDebit(Currency currency) {
        val account = new DebitAccount(idCounter.incrementAndGet(), currency, BigDecimal.ZERO);
        super.save(account);
        return account;
    }
}
