package repository.impl;

import domain.Account;
import domain.Transaction;
import domain.impl.Transfer;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicLong;

public class DefaultTransactionRepository extends BasicInMemoryRepository<Transaction, Long> {
    private static final AtomicLong idCounter = new AtomicLong();

    public Transfer createTransfer(Account sender, Account recipient, BigDecimal amount) {
        Transfer transfer = new Transfer(idCounter.incrementAndGet(), sender, recipient, amount);
        super.save(transfer);
        return transfer;
    }
}
