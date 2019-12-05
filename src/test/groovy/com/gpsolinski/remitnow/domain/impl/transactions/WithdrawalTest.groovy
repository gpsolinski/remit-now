/*
 * Copyright (c) 2019 Grzegorz Solinski. All rights reserved.
 */

package com.gpsolinski.remitnow.domain.impl.transactions

import com.gpsolinski.remitnow.domain.TransactionState
import com.gpsolinski.remitnow.domain.impl.accounts.SimpleBankAccount
import com.gpsolinski.remitnow.exceptions.InsufficientFundsException
import spock.lang.Specification

class WithdrawalTest extends Specification {

  def 'withdraw an amount from an account with sufficient funds'() {
    given: 'an account with non-zero balance'
    def initialBalance = 100.00
    def account = new SimpleBankAccount(1, initialBalance)

    and: 'an amount to withdraw'
    def amount = 50.00

    and: 'a new withdrawal transaction'
    def withdrawal = new Withdrawal(1, account, amount)

    when:
    withdrawal.complete()

    then: 'available balance is updated'
    account.availableBalance == initialBalance - amount

    and: 'the balance is not updated'
    account.balance == initialBalance

    and: 'transaction is in COMPLETED state'
    withdrawal.state == TransactionState.COMPLETED

  }

  def 'withdraw an amount from an account with insufficient funds'() {
    given: 'an account with non-zero balance'
    def initialBalance = 100.00
    def account = new SimpleBankAccount(1, initialBalance)

    and: 'an amount to withdraw'
    def amount = 150.00

    and: 'a new withdrawal transaction'
    def withdrawal = new Withdrawal(1, account, amount)

    when:
    withdrawal.complete()

    then:
    def e = thrown(InsufficientFundsException)
    e.message == "Insufficient balance in the account with ID ${account.id}"

    and: 'the account available balance is unchanged'
    account.availableBalance == initialBalance

    and: 'the transaction state is FAILED'
    withdrawal.state == TransactionState.FAILED
  }

  def 'get debit account'() {
    given:
    def account = new SimpleBankAccount(1)

    and:
    def withdrawal = new Withdrawal(1, account, 50.00)

    when:
    withdrawal.debitAccount

    then:
    thrown(UnsupportedOperationException)
  }
}
