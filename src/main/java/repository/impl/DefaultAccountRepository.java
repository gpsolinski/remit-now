package repository.impl;

import domain.Account;
import domain.impl.DebitAccount;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.concurrent.atomic.AtomicLong;

public class DefaultAccountRepository extends BasicInMemoryRepository<Account, Long> {
    private static final AtomicLong idCounter = new AtomicLong();

    public DebitAccount createDebit(Currency currency, BigDecimal initialBalance) {
        DebitAccount account = new DebitAccount(idCounter.incrementAndGet(), currency, initialBalance);
        super.save(account);
        return account;
    }

    @Override
    public void save(Account account) {
        account.updateBalance();
        super.save(account);
    }
}
