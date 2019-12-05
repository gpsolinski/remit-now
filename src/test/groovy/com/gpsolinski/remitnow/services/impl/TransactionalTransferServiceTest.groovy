package com.gpsolinski.remitnow.services.impl

import com.gpsolinski.remitnow.domain.impl.accounts.SimpleBankAccount
import com.gpsolinski.remitnow.domain.Account
import com.gpsolinski.remitnow.exceptions.InsufficientFundsException
import com.gpsolinski.remitnow.exceptions.TransferFailedException
import com.gpsolinski.remitnow.repository.BasicRepository
import spock.lang.Specification

class TransactionalTransferServiceTest extends Specification {

  def 'transfer from account with sufficient funds'() {
    given: 'a account with non-zero initial balance'
    def initialBalance = BigDecimal.valueOf(100)
    def sender = new SimpleBankAccount(id: 1, balance: initialBalance)

    and: 'another account with zero balance'
    def recipient = new SimpleBankAccount(id: 2)

    and: 'a valid amount'
    def amount = BigDecimal.valueOf(50)

    and: 'fake repository'
    def store = []
    def repository = new BasicRepository<Account, Long>() {
      @Override
      Optional<Account> findById(Long aLong) {
        return Optional.empty()
      }

      @Override
      Collection<Account> getAll() {
        return List.of()
      }

      @Override
      void save(Account bankAccount) {
        store.add(bankAccount)
      }
    }

    and: 'the transfer service'
    def underTest = new TransactionalTransferService(repository)

    when:
    underTest.transferFunds(sender, recipient, amount)

    then:
    sender.balance == initialBalance.subtract(amount)
    recipient.balance == amount
    store == [sender, recipient]
  }

  def 'transfer from account with insufficient funds'() {
    given: 'a account with non-zero initial balance'
    def initialBalance = BigDecimal.valueOf(50)
    def sender = new SimpleBankAccount(id: 1, balance: initialBalance)

    and: 'another account with zero balance'
    def recipient = new SimpleBankAccount(id: 2)

    and: 'an excessive amount'
    def amount = BigDecimal.valueOf(100)

    and: 'the transfer service'
    def underTest = new TransactionalTransferService()

    when:
    underTest.transferFunds(sender, recipient, amount)

    then:
    TransferFailedException e = thrown()
    e.cause.class == InsufficientFundsException
    e.message == "Couldn't credit " + amount + sender.getCurrency().getDisplayName() +
        " from a bank account with ID: 1"
  }
}
