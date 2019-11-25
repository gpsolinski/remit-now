package domain;

import exceptions.InsufficientFundsException;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;

public interface BankAccount<ID extends Serializable> extends Identifiable<ID> {
    BigDecimal getBalance();
    Currency getCurrency();
    void deposit(BigDecimal amount);
    void withdraw(BigDecimal amount) throws InsufficientFundsException;
}
