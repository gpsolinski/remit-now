package repository.impl

import domain.TransactionState
import services.impl.SimpleBankAccount
import spock.lang.Specification

class DefaultTransactionRepositoryTest extends Specification {

  def 'create transfer'() {
    given:
    def underTest = new DefaultTransactionRepository()

    and: 'an account to credit'
    def sender = new SimpleBankAccount(1)

    and: 'an account to debit'
    def recipient = new SimpleBankAccount(2)

    and: 'a non-zero amount'
    def amount = BigDecimal.valueOf(100)

    when:
    def transfer = underTest.createTransfer(sender, recipient, amount)

    then:
    transfer.id == 1
    transfer.state == TransactionState.NEW
    transfer.creditAccount == sender
    transfer.debitAccount == recipient
    transfer.amount == amount
  }
}
