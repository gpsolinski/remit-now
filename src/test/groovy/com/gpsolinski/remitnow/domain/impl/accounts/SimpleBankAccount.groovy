/*
 * Copyright (c) 2019 Grzegorz Solinski. All rights reserved.
 */

package com.gpsolinski.remitnow.domain.impl.accounts

import com.gpsolinski.remitnow.domain.Account
import com.gpsolinski.remitnow.exceptions.InsufficientFundsException

class SimpleBankAccount implements Account {
  final Long id
  BigDecimal balance
  BigDecimal availableBalance
  final Currency currency

  SimpleBankAccount(Long id, BigDecimal initialBalance = 0.00, Currency currency = Currency.getInstance(Locale.US)) {
    this.id = id
    this.balance = initialBalance
    this.availableBalance = initialBalance
    this.currency = currency
  }

  @Override
  void debit(BigDecimal amount) {
    availableBalance = availableBalance.add(amount)
  }

  @Override
  void credit(BigDecimal amount) throws InsufficientFundsException {
    if (availableBalance < amount) {
      throw new InsufficientFundsException(id)
    }
    availableBalance = availableBalance.subtract(amount)
  }

  @Override
  void updateBalance() {
    balance = availableBalance
  }
}
