package services;

import domain.BankAccount;
import exceptions.TransferFailedException;

import java.math.BigDecimal;

public interface TransferService<T extends BankAccount> {
    void transferFunds(T sender, T recipient, BigDecimal amount) throws TransferFailedException;
}
