/*
 * Copyright (c) 2019 Grzegorz Solinski. All rights reserved.
 */

package com.gpsolinski.remitnow.domain.impl.accounts

import com.gpsolinski.remitnow.exceptions.InsufficientFundsException
import spock.lang.Specification

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong
import java.util.stream.IntStream

class DebitAccountTest extends Specification {

  private AtomicLong idCounter = new AtomicLong()

  def 'sets the id, currency and initial balance'() {
    given:
    def id = 1
    def currency = Currency.getInstance('PLN')
    def initialBalance = 1000.00

    when:
    def account = new DebitAccount(id, currency, initialBalance)

    then:
    account.id == id
    account.currency == currency
    account.balance == initialBalance
  }

  def 'debit results in increased available balance'() {
    given:
    def initialBalance = 20.00
    def account = createUSDAccountWithBalance(initialBalance)

    and:
    def debitAmount = 100.00

    when:
    account.debit(debitAmount)

    then:
    account.availableBalance == initialBalance + debitAmount
  }

  def 'debit does not change the account balance'() {
    given:
    def initialBalance = 20.00
    def account = createUSDAccountWithBalance(initialBalance)

    and:
    def debitAmount = 100.00

    when:
    account.debit(debitAmount)

    then:
    account.balance == initialBalance
  }

  def 'credit with sufficient funds succeeds'() {
    given: 'an account with non-zero initial balance'
    def initialBalance = 100.00
    def account = createUSDAccountWithBalance(initialBalance)

    and: 'credit amount smaller than the current balance'
    def creditAmount = 50.00

    when:
    account.credit(creditAmount)

    then:
    account.availableBalance == initialBalance - creditAmount
  }

  def 'credit does not change the account balance'() {
    given: 'an account with non-zero initial balance'
    def initialBalance = 100.00
    def account = createUSDAccountWithBalance(initialBalance)

    and: 'credit amount smaller than the current balance'
    def creditAmount = 50.00

    when:
    account.credit(creditAmount)

    then:
    account.balance == initialBalance
  }

  def 'credit with insufficient funds results in exception'() {
    given: 'an account with non-zero initial balance'
    def initialBalance = 30.00
    def account = createUSDAccountWithBalance(initialBalance)

    and: 'credit amount larger than the current balance'
    def creditAmount = 50.00

    when:
    account.credit(creditAmount)

    then:
    thrown(InsufficientFundsException)
  }

  def 'debit by multiple threads results in accurate increase in available balance'() {
    given: 'an account with zero initial balance'
    def account = createUSDAccountWithBalance(0.00)

    and: 'an amount to add'
    def amount = 1.00
    def times = 1000

    and: 'an Executor Service'
    def service = Executors.newFixedThreadPool(5)

    when:
    IntStream.range(0, times).forEach {
      service.submit({ account.debit(amount) })
    }
    service.awaitTermination(1000, TimeUnit.MILLISECONDS)

    then:
    account.availableBalance == times * amount
  }

  def 'credit by multiple threads results in accurate decrease in available balance'() {
    given: 'an account with non-zero initial balance'
    def initialBalance = 1000.00
    def account = createUSDAccountWithBalance(initialBalance)

    and: 'an amount to credit'
    def amount = 1.00
    def times = 1000

    and: 'an Executor Service'
    def service = Executors.newFixedThreadPool(5)

    when:
    IntStream.range(0, times).forEach {
      service.submit({ account.credit(amount) })
    }
    service.awaitTermination(1000, TimeUnit.MILLISECONDS)

    then:
    account.availableBalance == 0.00
  }

  def 'updates account balance'() {
    given: 'an account with non-zero initial balance'
    def initialBalance = 100.00
    def account = createUSDAccountWithBalance(initialBalance)

    and: 'some valid transactions'
    account.debit(250.00)
    account.credit(300.00)

    and: 'updated available balance'
    assert account.availableBalance == 50

    when:
    account.updateBalance()

    then:
    account.balance == account.availableBalance
  }

  private DebitAccount createUSDAccountWithBalance(BigDecimal balance) {
    return new DebitAccount(idCounter.getAndIncrement(), Currency.getInstance(Locale.US), balance)
  }
}
