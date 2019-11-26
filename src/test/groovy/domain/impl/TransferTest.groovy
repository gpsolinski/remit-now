package domain.impl

import domain.TransactionState
import exceptions.InsufficientFundsException
import services.impl.SimpleBankAccount
import spock.lang.Specification

class TransferTest extends Specification {

  def 'transfer is created in NEW state'() {
    when:
    def transfer = new Transfer(1, null, null, BigDecimal.TEN)

    then:
    transfer.state == TransactionState.NEW
  }

  def 'complete transfer from account with sufficient funds'() {
    given: 'a account with non-zero initial balance'
    def initialBalance = BigDecimal.valueOf(100)
    def sender = new SimpleBankAccount(1, initialBalance)

    and: 'another account with zero balance'
    def recipient = new SimpleBankAccount(2)

    and: 'a valid amount'
    def amount = BigDecimal.valueOf(50)

    and: 'a money transfer between these accounts'
    def transfer = new Transfer(1, sender, recipient, amount)

    when:
    transfer.complete()

    then:
    sender.availableBalance == initialBalance.subtract(amount)
    sender.balance == initialBalance
    recipient.availableBalance == amount
    recipient.balance == BigDecimal.ZERO
    transfer.state == TransactionState.COMPLETED
  }

  def 'order transfer from account with insufficient funds'() {
    given: 'a account with non-zero initial balance'
    def initialBalance = BigDecimal.valueOf(20)
    def sender = new SimpleBankAccount(1, initialBalance)

    and: 'another account with zero balance'
    def recipient = new SimpleBankAccount(2)

    and: 'an excessive amount'
    def amount = BigDecimal.valueOf(50)

    and: 'a money transfer between these accounts'
    def transfer = new Transfer(1, sender, recipient, amount)

    when:
    transfer.complete()

    then:
    thrown(InsufficientFundsException)
    sender.availableBalance == initialBalance
    sender.balance == initialBalance
    recipient.availableBalance == BigDecimal.ZERO
    recipient.balance == BigDecimal.ZERO
    transfer.state == TransactionState.FAILED
  }
}
