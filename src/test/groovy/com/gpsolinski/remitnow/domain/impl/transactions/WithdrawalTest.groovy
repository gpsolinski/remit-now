package com.gpsolinski.remitnow.domain.impl.transactions

import com.gpsolinski.remitnow.domain.TransactionState
import com.gpsolinski.remitnow.domain.impl.accounts.SimpleBankAccount
import spock.lang.Specification

class WithdrawalTest extends Specification {

  def 'withdraw an amount from an account with sufficient funds'() {
    given: 'an account with non-zero balance'
    def initialBalance = BigDecimal.valueOf(100)
    def account = new SimpleBankAccount(1, initialBalance)

    and: 'an amount to withdraw'
    def amount = BigDecimal.valueOf(50)

    and: 'a new withdrawal transaction'
    def withdrawal = new Withdrawal(1, account, amount)

    when:
    withdrawal.complete()

    then:
    withdrawal.state == TransactionState.COMPLETED
    account.availableBalance == initialBalance - amount
  }

  def 'get debit account'() {
    given:
    def account = new SimpleBankAccount(1)

    and:
    def withdrawal = new Withdrawal(1, account, BigDecimal.valueOf(50))

    when:
    withdrawal.debitAccount

    then:
    thrown(UnsupportedOperationException)
  }
}
