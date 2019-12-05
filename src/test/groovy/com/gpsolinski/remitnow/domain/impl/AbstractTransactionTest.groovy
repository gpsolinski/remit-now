package com.gpsolinski.remitnow.domain.impl

import com.gpsolinski.remitnow.domain.Account
import com.gpsolinski.remitnow.domain.TransactionState
import com.gpsolinski.remitnow.exceptions.InsufficientFundsException
import spock.lang.Specification

class AbstractTransactionTest extends Specification {

  def 'transaction is created in the NEW state'() {
    when: 'creating a new object of a class extending AbstractTransaction'
    def transaction = new AbstractTransaction(1, BigDecimal.valueOf(50)) {
      @Override
      Account getCreditAccount() {
        return null
      }

      @Override
      Account getDebitAccount() {
        return null
      }

      @Override
      void complete() throws InsufficientFundsException {

      }
    }

    then:
    transaction.state == TransactionState.NEW
  }

  def 'validate incomplete transaction state'() {
    given: 'an abstract transaction instance'
    def transaction = new AbstractTransaction(1, BigDecimal.valueOf(50)) {
      @Override
      Account getCreditAccount() {
        return null
      }

      @Override
      Account getDebitAccount() {
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
    def transaction = new AbstractTransaction(1, BigDecimal.valueOf(50)) {
      @Override
      Account getCreditAccount() {
        return null
      }

      @Override
      Account getDebitAccount() {
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
