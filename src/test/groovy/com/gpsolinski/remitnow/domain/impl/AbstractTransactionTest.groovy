/*
 * Copyright (c) 2019 Grzegorz Solinski. All rights reserved.
 */

package com.gpsolinski.remitnow.domain.impl

import com.gpsolinski.remitnow.domain.Account
import com.gpsolinski.remitnow.domain.TransactionState
import com.gpsolinski.remitnow.exceptions.InsufficientFundsException
import spock.lang.Specification

class AbstractTransactionTest extends Specification {

  def 'transaction is created in the NEW state'() {
    when: 'creating a new object of a class extending AbstractTransaction'
    def transaction = new AbstractTransaction(1, 50.00) {
      @Override
      Account getCreditAccount() {
        return null
      }

      @Override
      Account getDebitAccount() {
        return null
      }

      @Override
      TransactionType getType() {
        return null
      }

      @Override
      void complete() throws InsufficientFundsException {

      }
    }

    then:
    transaction.state == TransactionState.NEW
  }

  def 'create transaction with negative amount'() {
    when: 'creating a new object of a class extending AbstractTransaction'
    def transaction = new AbstractTransaction(1, -10.00) {
      @Override
      Account getCreditAccount() {
        return null
      }

      @Override
      Account getDebitAccount() {
        return null
      }

      @Override
      TransactionType getType() {
        return null
      }

      @Override
      void complete() throws InsufficientFundsException {

      }
    }

    then:
    def e = thrown(IllegalArgumentException)
    e.message == 'Transaction amount needs to be positive'
  }

  def 'create transaction with zero amount'() {
    when: 'creating a new object of a class extending AbstractTransaction'
    def transaction = new AbstractTransaction(1, 0.00) {
      @Override
      Account getCreditAccount() {
        return null
      }

      @Override
      Account getDebitAccount() {
        return null
      }

      @Override
      TransactionType getType() {
        return null
      }

      @Override
      void complete() throws InsufficientFundsException {

      }
    }

    then:
    def e = thrown(IllegalArgumentException)
    e.message == 'Transaction amount needs to be positive'
  }

  def 'validate incomplete transaction state'() {
    given: 'an abstract transaction instance'
    def transaction = new AbstractTransaction(1, 50.00) {
      @Override
      Account getCreditAccount() {
        return null
      }

      @Override
      Account getDebitAccount() {
        return null
      }

      @Override
      TransactionType getType() {
        return null
      }

      @Override
      void complete() throws InsufficientFundsException {

      }

      void setState(TransactionState state) {
        super.state.setPlain(state)
      }
    }

    and:
    transaction.state = state

    when:
    transaction.validateIncompleteState()

    then:
    noExceptionThrown()

    where:
    state << [TransactionState.NEW, TransactionState.FAILED]
  }

  def 'validate incomplete transaction state fails when in the COMPLETED state'() {
    given: 'an abstract transaction instance'
    def transaction = new AbstractTransaction(1, 50.00) {
      @Override
      Account getCreditAccount() {
        return null
      }

      @Override
      Account getDebitAccount() {
        return null
      }

      @Override
      TransactionType getType() {
        return null
      }

      @Override
      void complete() throws InsufficientFundsException {

      }

      void setState(TransactionState state) {
        super.state.setPlain(state)
      }
    }

    and:
    transaction.state = TransactionState.COMPLETED

    when:
    transaction.validateIncompleteState()

    then:
    thrown(IllegalStateException)
  }
}
