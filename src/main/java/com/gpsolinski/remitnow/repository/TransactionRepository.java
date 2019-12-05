package com.gpsolinski.remitnow.repository;

import com.gpsolinski.remitnow.domain.Account;
import com.gpsolinski.remitnow.domain.Transaction;
import com.gpsolinski.remitnow.domain.impl.transactions.Deposit;
import com.gpsolinski.remitnow.domain.impl.transactions.Transfer;
import com.gpsolinski.remitnow.domain.impl.transactions.Withdrawal;

import java.math.BigDecimal;

public interface TransactionRepository extends BasicRepository<Transaction, Long> {
    Deposit createDeposit(Account account, BigDecimal amount);
    Withdrawal createWithdrawal(Account account, BigDecimal amount);
    Transfer createTransfer(Account sender, Account recipient, BigDecimal amount);
}
