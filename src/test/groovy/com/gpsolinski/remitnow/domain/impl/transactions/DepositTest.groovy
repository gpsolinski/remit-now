package com.gpsolinski.remitnow.domain.impl.transactions

import com.gpsolinski.remitnow.domain.TransactionState
import com.gpsolinski.remitnow.domain.impl.accounts.SimpleBankAccount
import spock.lang.Specification

class DepositTest extends Specification {

  def 'deposit an amount to the account'() {
    given: 'an account with zero balance'
    def account = new SimpleBankAccount(1)

    and: 'an amount to deposit'
    def amount = BigDecimal.valueOf(100)

    and: 'a new deposit transaction'
    def deposit = new Deposit(1, account, amount)

    when:
    deposit.complete()

    then:
    deposit.state == TransactionState.COMPLETED
    account.availableBalance == amount
  }

  def 'get credit account'() {
    given:
    def account = new SimpleBankAccount(1)

    and:
    def deposit = new Deposit(1, account, BigDecimal.valueOf(50))

    when:
    deposit.creditAccount

    then:
    thrown(UnsupportedOperationException)
  }
}
