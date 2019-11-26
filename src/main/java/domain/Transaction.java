package domain;

import exceptions.InsufficientFundsException;

import java.math.BigDecimal;

public interface Transaction extends Identifiable<Long> {
    Account getCreditAccount();
    Account getDebitAccount();
    BigDecimal getAmount();
    TransactionState getState();
    void complete() throws InsufficientFundsException;
}
