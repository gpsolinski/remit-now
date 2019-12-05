/*
 * Copyright (c) 2019 Grzegorz Solinski. All rights reserved.
 */

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
}
