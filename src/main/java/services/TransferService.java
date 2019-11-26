package services;

import domain.Account;
import exceptions.TransferFailedException;

import java.math.BigDecimal;

public interface TransferService<T extends Account> {
    void transferFunds(T sender, T recipient, BigDecimal amount) throws TransferFailedException;
}
