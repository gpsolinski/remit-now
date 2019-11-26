package services.impl

import exceptions.InsufficientFundsException
import exceptions.TransferFailedException
import spock.lang.Specification

class TransactionalTransferServiceTest extends Specification {

  def 'transfer from account with sufficient funds'() {
    given: 'a account with non-zero initial balance'
    def initialBalance = BigDecimal.valueOf(100)
    def sender = new SimpleBankAccount(ID: 1, balance: initialBalance)

    and: 'another account with zero balance'
    def recipient = new SimpleBankAccount(ID: 2)

    and: 'a valid amount'
    def amount = BigDecimal.valueOf(50)

    and: 'the transfer service'
    def underTest = new TransactionalTransferService()

    when:
    underTest.transferFunds(sender, recipient, amount)

    then:
    sender.balance == initialBalance.subtract(amount)
    recipient.balance == amount
  }

  def 'transfer from account with insufficient funds'() {
    given: 'a account with non-zero initial balance'
    def initialBalance = BigDecimal.valueOf(50)
    def sender = new SimpleBankAccount(ID: 1, balance: initialBalance)

    and: 'another account with zero balance'
    def recipient = new SimpleBankAccount(ID: 2)

    and: 'an excessive amount'
    def amount = BigDecimal.valueOf(100)

    and: 'the transfer service'
    def underTest = new TransactionalTransferService()

    when:
    underTest.transferFunds(sender, recipient, amount)

    then:
    TransferFailedException e = thrown()
    e.cause.class == InsufficientFundsException
    e.message == "Couldn't withdraw " + amount + sender.getCurrency().getDisplayName() +
        " from a bank account with ID: 1"
  }
}
