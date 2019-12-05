package com.gpsolinski.remitnow.services.impl

import com.gpsolinski.remitnow.domain.Account
import com.gpsolinski.remitnow.domain.Transaction
import com.gpsolinski.remitnow.domain.TransactionState
import com.gpsolinski.remitnow.domain.impl.accounts.SimpleBankAccount
import com.gpsolinski.remitnow.domain.impl.transactions.Deposit
import com.gpsolinski.remitnow.domain.impl.transactions.Transfer
import com.gpsolinski.remitnow.domain.impl.transactions.Withdrawal
import com.gpsolinski.remitnow.exceptions.InsufficientFundsException
import com.gpsolinski.remitnow.repository.AccountRepository
import com.gpsolinski.remitnow.repository.TransactionRepository
import spock.lang.Specification

class TransactionalTransferServiceTest extends Specification {

  def 'transfer from account with sufficient funds'() {
    given: 'a account with non-zero initial balance'
    def initialBalance = BigDecimal.valueOf(100)
    def sender = new SimpleBankAccount(1, initialBalance)

    and: 'another account with zero balance'
    def recipient = new SimpleBankAccount( 2)

    and: 'a valid amount'
    def transferAmount = BigDecimal.valueOf(50)

    and: 'fake account repository'
    def accountStore = []
    def accRepository = createFakeAccountRepository(accountStore)

    and: 'a fake transaction repository'
    def transactionStore = []
    def transferId = 1
    def transferProvider = { credit, debit, trAmount -> return new Transfer(transferId, credit, debit, trAmount)}
    def trRepository = createFakeTransactionRepository(transactionStore, transferProvider)

    and: 'the transfer service'
    def underTest = new TransactionalTransferService(accRepository, trRepository)

    when:
    def transfer = underTest.transferFunds(sender, recipient, transferAmount)

    then: 'the transfer is created and completed'
    with (transfer) {
      amount == transferAmount
      creditAccount == sender
      debitAccount == recipient
      state == TransactionState.COMPLETED
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
    def initialBalance = BigDecimal.valueOf(50)
    def sender = new SimpleBankAccount(1, initialBalance)

    and: 'another account with zero balance'
    def recipient = new SimpleBankAccount(2)

    and: 'an excessive amount'
    def amount = BigDecimal.valueOf(100)

    and: 'fake account repository'
    def accountStore = []
    def accRepository = createFakeAccountRepository(accountStore)

    and: 'a fake transaction repository'
    def transactionStore = []
    def transferId = 1L
    def transferProvider = { credit, debit, trAmount -> return new Transfer(transferId, credit, debit, trAmount)}
    def trRepository = createFakeTransactionRepository(transactionStore, transferProvider)

    and: 'the transfer service'
    def underTest = new TransactionalTransferService(accRepository, trRepository)

    when:
    underTest.transferFunds(sender, recipient, amount)

    then:
    def e = thrown(InsufficientFundsException)
    e.message == "Insufficient balance in the account with ID ${sender.id}"

    and: 'the transfer was created and saved in FAILED state'
    def maybeTransfer = trRepository.findById(transferId)
    maybeTransfer.present
    with (maybeTransfer.get()) {
      amount == amount
      creditAccount == sender
      debitAccount == recipient
      state == TransactionState.FAILED
    }

    and: 'the accounts balance is unchanged'
    sender.balance == initialBalance
    recipient.balance == 0.00
  }

  def createFakeAccountRepository(List<Account> store) {
    return new AccountRepository() {
      @Override
      Optional<Account> findById(Long id) {
        return Optional.ofNullable(store.find {it.id == id})
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

  def createFakeTransactionRepository(List<Transaction> store, Closure<Transfer> transferProvider) {
    new TransactionRepository() {
      @Override
      Optional<Transaction> findById(Long id) {
        return Optional.ofNullable(store.find {it.id == id})
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
        return null
      }

      @Override
      Withdrawal createWithdrawal(Account account, BigDecimal amount) {
        return null
      }

      @Override
      Transfer createTransfer(Account sender, Account recipient, BigDecimal amount) {
        return transferProvider(sender, recipient, amount)
      }
    }
  }
}
