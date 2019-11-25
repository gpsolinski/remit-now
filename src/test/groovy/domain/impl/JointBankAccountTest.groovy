package domain.impl

import exceptions.InsufficientFundsException
import spock.lang.Specification

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.stream.IntStream

class JointBankAccountTest extends Specification {

  def 'default currency is USD'() {
    when:
    def account = new JointBankAccount()

    then:
    account.currency == Currency.getInstance(Locale.US)
  }

  def 'newly created account has zero balance'() {
    when:
    def account = new JointBankAccount()

    then:
    account.balance == BigDecimal.ZERO
  }

  def 'sets currency and initial balance'() {
    given:
    def currency = Currency.getInstance('PLN')
    def initialBalance = BigDecimal.valueOf(1000)

    when:
    def account = new JointBankAccount(currency, initialBalance)

    then:
    account.currency == currency
    account.balance == initialBalance

  }

  def 'deposit results in increased balance'() {
    given:
    def initialBalance = BigDecimal.valueOf(20)
    def account = new JointBankAccount(Currency.getInstance(Locale.US), initialBalance)

    and:
    def depositAmount = BigDecimal.valueOf(100)

    when:
    account.deposit(depositAmount)

    then:
    account.balance == initialBalance + depositAmount
  }

  def 'withdraw with sufficient funds succeeds'() {
    given: 'an account with non-zero initial balance'
    def initialBalance = BigDecimal.valueOf(100)
    def account = new JointBankAccount(Currency.getInstance(Locale.US), initialBalance)

    and: 'withdraw amount smaller than the current balance'
    def withdrawalAmount = BigDecimal.valueOf(50)

    when:
    account.withdraw(withdrawalAmount)

    then:
    account.balance == initialBalance - withdrawalAmount
  }

  def 'withdraw with insufficient funds results in exception'() {
    given: 'an account with non-zero initial balance'
    def initialBalance = BigDecimal.valueOf(30)
    def account = new JointBankAccount(Currency.getInstance(Locale.US), initialBalance)

    and: 'withdraw amount larger than the current balance'
    def withdrawalAmount = BigDecimal.valueOf(50)

    when:
    account.withdraw(withdrawalAmount)

    then:
    thrown(InsufficientFundsException)
  }

  def 'deposit by multiple threads results in accurate increase in balance'() {
    given: 'an account with zero initial balance'
    def account = new JointBankAccount()

    and: 'an amount to add'
    def amount = BigDecimal.ONE
    def times = 1000

    and: 'an Executor Service'
    def service = Executors.newFixedThreadPool(5)

    when:
    IntStream.range(0, times).forEach {
      service.submit({ account.deposit(amount) })
    }
    service.awaitTermination(1000, TimeUnit.MILLISECONDS)

    then:
    account.balance == BigDecimal.valueOf(times)
  }

  def 'withdraw by multiple threads results in accurate decrease in balance'() {
    given: 'an account with non-zero initial balance'
    def initialBalance = BigDecimal.valueOf(1000)
    def account = new JointBankAccount(Currency.getInstance(Locale.US), initialBalance)

    and: 'an amount to withdraw'
    def amount = BigDecimal.valueOf(1)
    def times = 1000

    and: 'an Executor Service'
    def service = Executors.newFixedThreadPool(5)

    when:
    IntStream.range(0, times).forEach {
      service.submit({ account.withdraw(amount) })
    }
    service.awaitTermination(1000, TimeUnit.MILLISECONDS)

    then:
    account.balance == BigDecimal.ZERO
  }
}
