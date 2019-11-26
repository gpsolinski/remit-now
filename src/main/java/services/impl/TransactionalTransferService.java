package services.impl;

import domain.BankAccount;
import exceptions.InsufficientFundsException;
import exceptions.TransferFailedException;
import services.TransferService;

import java.math.BigDecimal;

public class TransactionalTransferService implements TransferService<BankAccount> {
    @Override
    public void transferFunds(BankAccount sender, BankAccount recipient, BigDecimal amount) throws TransferFailedException {
        try {
            sender.withdraw(amount);
            recipient.deposit(amount);
        } catch (InsufficientFundsException e) {
            throw new TransferFailedException("Couldn't withdraw " + amount + sender.getCurrency().getDisplayName() +
                    " from a bank account with ID: " + sender.getID(), e);
        }
    }
}
