/*
 * Copyright (c) 2019 Grzegorz Solinski. All rights reserved.
 */

package com.gpsolinski.remitnow.domain.impl.transactions

import com.gpsolinski.remitnow.domain.TransactionState
import com.gpsolinski.remitnow.domain.impl.accounts.SimpleBankAccount
import com.gpsolinski.remitnow.exceptions.InsufficientFundsException
import spock.lang.Specification

class TransferTest extends Specification {

  def 'complete transfer from account with sufficient funds'() {
    given: 'a account with non-zero initial balance'
    def initialBalance = 100.00
    def sender = new SimpleBankAccount(1, initialBalance)

    and: 'another account with zero balance'
    def recipient = new SimpleBankAccount(2)

    and: 'a valid amount'
    def amount = 50.00

    and: 'a money transfer between these accounts'
    def transfer = new Transfer(1, sender, recipient, amount)

    when:
    transfer.complete()

    then:
    sender.availableBalance == initialBalance.subtract(amount)
    sender.balance == initialBalance
    recipient.availableBalance == amount
    recipient.balance == 0.00
    transfer.state == TransactionState.COMPLETED
  }

  def 'order transfer from account with insufficient funds'() {
    given: 'a account with non-zero initial balance'
    def initialBalance = 20.00
    def sender = new SimpleBankAccount(1, initialBalance)

    and: 'another account with zero balance'
    def recipient = new SimpleBankAccount(2)

    and: 'an excessive amount'
    def amount = 50.00

    and: 'a money transfer between these accounts'
    def transfer = new Transfer(1, sender, recipient, amount)

    when:
    transfer.complete()

    then:
    thrown(InsufficientFundsException)
    sender.availableBalance == initialBalance
    sender.balance == initialBalance
    recipient.availableBalance == 0.00
    recipient.balance == 0.00
    transfer.state == TransactionState.FAILED
  }
}
