package services.impl;

import domain.Account;
import exceptions.InsufficientFundsException;
import exceptions.TransferFailedException;
import repository.BasicRepository;
import services.TransferService;

import java.math.BigDecimal;

public class TransactionalTransferService implements TransferService<Account> {

    private final BasicRepository<Account, Long> repository;

    public TransactionalTransferService(BasicRepository<Account, Long> repository) {
        this.repository = repository;
    }

    @Override
    public void transferFunds(Account sender, Account recipient, BigDecimal amount) throws TransferFailedException {
        try {
            sender.credit(amount);
            recipient.debit(amount);
            repository.save(sender);
            repository.save(recipient);
        } catch (InsufficientFundsException e) {
            throw new TransferFailedException("Couldn't credit " + amount + sender.getCurrency().getDisplayName() +
                    " from a bank account with ID: " + sender.getId(), e);
        } catch (RuntimeException e) {

        }
    }
}
