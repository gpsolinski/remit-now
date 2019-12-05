package com.gpsolinski.remitnow.repository.impl;

import com.gpsolinski.remitnow.domain.Account;
import com.gpsolinski.remitnow.domain.impl.accounts.DebitAccount;
import com.gpsolinski.remitnow.repository.AccountRepository;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.concurrent.atomic.AtomicLong;

public class DefaultAccountRepository extends BasicInMemoryRepository<Account, Long> implements AccountRepository {
    private static final AtomicLong idCounter = new AtomicLong();

    @Override
    public DebitAccount createDebit(Currency currency) {
        DebitAccount account = new DebitAccount(idCounter.incrementAndGet(), currency, BigDecimal.ZERO);
        super.save(account);
        return account;
    }

    @Override
    public void save(Account account) {
        account.updateBalance();
        super.save(account);
    }
}
