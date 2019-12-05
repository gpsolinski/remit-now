/*
 * Copyright (c) 2019 Grzegorz Solinski. All rights reserved.
 */

package com.gpsolinski.remitnow.services.impl

import com.gpsolinski.remitnow.domain.Account
import com.gpsolinski.remitnow.domain.Transaction
import com.gpsolinski.remitnow.domain.TransactionState
import com.gpsolinski.remitnow.domain.impl.TransactionType
import com.gpsolinski.remitnow.domain.impl.accounts.SimpleBankAccount
import com.gpsolinski.remitnow.domain.impl.transactions.Deposit
import com.gpsolinski.remitnow.domain.impl.transactions.Transfer
import com.gpsolinski.remitnow.domain.impl.transactions.Withdrawal
import com.gpsolinski.remitnow.exceptions.InsufficientFundsException
import com.gpsolinski.remitnow.repository.AccountRepository
import com.gpsolinski.remitnow.repository.TransactionRepository
import spock.lang.Specification

class DefaultTransactionServiceTest extends Specification {

  def 'transfer from account with sufficient funds'() {
    given: 'a account with non-zero initial balance'
    def initialBalance = 100.00
    def sender = new SimpleBankAccount(1, initialBalance)

    and: 'another account with zero balance'
    def recipient = new SimpleBankAccount(2)

    and: 'a valid amount'
    def transferAmount = 50.00

    and: 'fake account repository'
    def accountStore = []
    def accRepository = createFakeAccountRepository(accountStore)

    and: 'a fake transaction repository'
    def transactionStore = []
    def transferId = 1
    def transferSupplier = { credit, debit, trAmount -> return new Transfer(transferId, credit, debit, trAmount) }
    def trRepository = createFakeTransferRepository(transactionStore, transferSupplier)

    and: 'the transfer service'
    def underTest = new DefaultTransactionService(accRepository, trRepository)

    when:
    def transfer = underTest.transferFunds(sender, recipient, transferAmount)

    then: 'the transfer is created and completed'
    with(transfer) {
      amount == transferAmount
      creditAccount == sender
      debitAccount == recipient
      state == TransactionState.COMPLETED
      type == TransactionType.TRANSFER
    }

    and: 'the accounts have their balance updated'
    sender.balance == initialBalance.subtract(transferAmount)
    recipient.balance == transferAmount

    and: 'account contains the updated accounts'
    accountStore == [sender, recipient]

    and: 'transaction store contains the transfer'
    transactionStore == [transfer]
  }

  def 'transfer from account with insufficient funds'() {
    given: 'a account with non-zero initial balance'
    def initialBalance = 50.00
    def sender = new SimpleBankAccount(1, initialBalance)

    and: 'another account with zero balance'
    def recipient = new SimpleBankAccount(2)

    and: 'an excessive amount'
    def amount = 100.00

    and: 'fake account repository'
    def accountStore = []
    def accRepository = createFakeAccountRepository(accountStore)

    and: 'a fake transaction repository'
    def transactionStore = []
    def transferId = 1L
    def transferSupplier = { credit, debit, trAmount -> return new Transfer(transferId, credit, debit, trAmount) }
    def trRepository = createFakeTransferRepository(transactionStore, transferSupplier)

    and: 'the transfer service'
    def underTest = new DefaultTransactionService(accRepository, trRepository)

    when:
    underTest.transferFunds(sender, recipient, amount)

    then:
    def e = thrown(InsufficientFundsException)
    e.message == "Insufficient balance in the account with ID ${sender.id}"

    and: 'the transfer was created and saved in FAILED state'
    def maybeTransfer = trRepository.findById(transferId)
    maybeTransfer.present
    with(maybeTransfer.get()) {
      amount == amount
      creditAccount == sender
      debitAccount == recipient
      state == TransactionState.FAILED
      type == TransactionType.TRANSFER
    }

    and: 'the accounts balance is unchanged'
    sender.balance == initialBalance
    sender.availableBalance == initialBalance
    recipient.balance == 0.00
    recipient.availableBalance == 0.00
  }

  def 'deposit to an account'() {
    given: 'a account with zero initial balance'
    def account = new SimpleBankAccount(1)

    and: 'an amount to deposit'
    def depositAmount = 50.00

    and: 'fake account repository'
    def accountStore = []
    def accRepository = createFakeAccountRepository(accountStore)

    and: 'a fake transaction repository'
    def transactionStore = []
    def depositId = 1
    def depositSupplier = { acc, depAmount -> return new Deposit(depositId, acc, depAmount) }
    def trRepository = createFakeDepositRepository(transactionStore, depositSupplier)

    and: 'the transaction service'
    def underTest = new DefaultTransactionService(accRepository, trRepository)

    when:
    def deposit = underTest.depositFunds(account, depositAmount)

    then: 'the deposit is created and completed'
    with (deposit) {
      amount == depositAmount
      debitAccount == account
      state == TransactionState.COMPLETED
      type == TransactionType.DEPOSIT
    }

    and: 'the account has its balance updated'
    account.availableBalance == depositAmount
    account.balance == depositAmount

    and: 'account contains the updated accounts'
    accountStore == [account]

    and: 'transaction store contains the transfer'
    transactionStore == [deposit]
  }

  def 'withdraw from an account with sufficient funds'() {
    given: 'a account with non-zero initial balance'
    def initialBalance = 100.00
    def account = new SimpleBankAccount(1, initialBalance)

    and: 'a valid amount to withdraw'
    def withdrawalAmount = 50.00

    and: 'fake account repository'
    def accountStore = []
    def accRepository = createFakeAccountRepository(accountStore)

    and: 'a fake transaction repository'
    def transactionStore = []
    def withdrawalId = 1
    def withdrawalSupplier = { acc, witAmount -> return new Withdrawal(withdrawalId, acc, witAmount) }
    def trRepository = createFakeWithdrawalRepository(transactionStore, withdrawalSupplier)

    and: 'the transaction service'
    def underTest = new DefaultTransactionService(accRepository, trRepository)

    when:
    def withdrawal = underTest.withdrawFunds(account, withdrawalAmount)

    then: 'the withdrawal is created and completed'
    with (withdrawal) {
      amount == withdrawalAmount
      creditAccount == account
      state == TransactionState.COMPLETED
      type == TransactionType.WITHDRAWAL
    }

    and: 'the account has its balance updated'
    account.availableBalance == initialBalance - withdrawalAmount
    account.balance == initialBalance - withdrawalAmount

    and: 'account contains the updated accounts'
    accountStore == [account]

    and: 'transaction store contains the transfer'
    transactionStore == [withdrawal]
  }

  def 'withdraw from an account with insufficient funds'() {
    given: 'a account with non-zero initial balance'
    def initialBalance = 100.00
    def account = new SimpleBankAccount(1, initialBalance)

    and: 'an excessive amount to withdraw'
    def withdrawalAmount = 150.00

    and: 'fake account repository'
    def accountStore = []
    def accRepository = createFakeAccountRepository(accountStore)

    and: 'a fake transaction repository'
    def transactionStore = []
    def withdrawalId = 1L
    def withdrawalSupplier = { acc, witAmount -> return new Withdrawal(withdrawalId, acc, witAmount) }
    def trRepository = createFakeWithdrawalRepository(transactionStore, withdrawalSupplier)

    and: 'the transaction service'
    def underTest = new DefaultTransactionService(accRepository, trRepository)

    when:
    underTest.withdrawFunds(account, withdrawalAmount)

    then:
    thrown(InsufficientFundsException)

    and: 'the transaction was created and saved in FAILED state'
    def maybeTransfer = trRepository.findById(withdrawalId)
    maybeTransfer.present
    with(maybeTransfer.get()) {
      amount == amount
      creditAccount == account
      state == TransactionState.FAILED
      type == TransactionType.WITHDRAWAL
    }

    and: 'the account balance is unchanged'
    account.balance == initialBalance
    account.availableBalance == initialBalance
  }

  def createFakeAccountRepository(List<Account> store) {
    return new AccountRepository() {
      @Override
      Optional<Account> findById(Long id) {
        return Optional.ofNullable(store.find { it.id == id })
      }

      @Override
      Collection<Account> getAll() {
        return store
      }

      @Override
      void save(Account bankAccount) {
        store.add(bankAccount)
      }

      @Override
      Account createDebit(Currency currency) {
        return null
      }
    }
  }

  def createFakeDepositRepository(List<Transaction> store, Closure<Deposit> depositSupplier) {
    return createFakeTransactionRepository(store, depositSupplier, null, null)
  }

  def createFakeWithdrawalRepository(List<Transaction> store, Closure<Withdrawal> withdrawalSupplier) {
    return createFakeTransactionRepository(store, null, withdrawalSupplier, null)
  }

  def createFakeTransferRepository(List<Transaction> store, Closure<Transfer> transferSupplier) {
    return createFakeTransactionRepository(store, null, null, transferSupplier)
  }

  def createFakeTransactionRepository(List<Transaction> store,
                                      Closure<Deposit> depositSupplier = null,
                                      Closure<Withdrawal> withdrawalSupplier = null,
                                      Closure<Transfer> transferSupplier = null) {
    new TransactionRepository() {
      @Override
      Optional<Transaction> findById(Long id) {
        return Optional.ofNullable(store.find { it.id == id })
      }

      @Override
      Collection<Transaction> getAll() {
        return store
      }

      @Override
      void save(Transaction transaction) {
        store.add(transaction)
      }

      @Override
      Deposit createDeposit(Account account, BigDecimal amount) {
        return depositSupplier(account, amount)
      }

      @Override
      Withdrawal createWithdrawal(Account account, BigDecimal amount) {
        return withdrawalSupplier(account, amount)
      }

      @Override
      Transfer createTransfer(Account sender, Account recipient, BigDecimal amount) {
        return transferSupplier(sender, recipient, amount)
      }
    }
  }
}
