package com.gpsolinski.remitnow.repository.impl

import spock.lang.Specification

class DefaultAccountRepositoryTest extends Specification {

  def 'create debit account'() {
    given:
    def accountRepository = new DefaultAccountRepository()

    when:
    def account = accountRepository.createDebit(Currency.getInstance(Locale.US))

    then:
    account.id == 1
    account.currency == Currency.getInstance(Locale.US)
    account.balance == BigDecimal.ZERO
    account.availableBalance == BigDecimal.ZERO
  }

  def 'saving account updates the balance'() {
    given:
    def accountRepository = new DefaultAccountRepository()

    and: 'a debit account'
    def account = accountRepository.createDebit(Currency.getInstance(Locale.US))

    and: 'some transactions'
    account.debit(BigDecimal.valueOf(100))
    account.credit(BigDecimal.valueOf(30))

    and:
    assert account.availableBalance != account.balance

    when:
    accountRepository.save(account)

    then:
    account.balance == account.availableBalance
  }

}
