package com.gpsolinski.remitnow.repository.impl

import com.gpsolinski.remitnow.domain.TransactionState
import com.gpsolinski.remitnow.domain.impl.accounts.SimpleBankAccount
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

  def 'create deposit'() {
    given:
    def underTest = new DefaultTransactionRepository()

    and: 'an account to debit'
    def recipient = new SimpleBankAccount(1)

    and: 'a non-zero amount'
    def amount = BigDecimal.valueOf(100)

    when:
    def deposit = underTest.createDeposit(recipient, amount)

    then:
    deposit.id == 1
    deposit.state == TransactionState.NEW
    deposit.debitAccount == recipient
    deposit.amount == amount
  }

  def 'create withdrawal'() {
    given:
    def underTest = new DefaultTransactionRepository()

    and: 'an account to debit with non-zero initial balance'
    def initialBalance = BigDecimal.valueOf(100)
    def account = new SimpleBankAccount(1, initialBalance)

    and: 'an amount to withdraw'
    def amount = BigDecimal.valueOf(50)

    when:
    def withdrawal = underTest.createWithdrawal(account, amount)

    then:
    withdrawal.id == 1
    withdrawal.state == TransactionState.NEW
    withdrawal.creditAccount == account
    withdrawal.amount == amount
  }
}
