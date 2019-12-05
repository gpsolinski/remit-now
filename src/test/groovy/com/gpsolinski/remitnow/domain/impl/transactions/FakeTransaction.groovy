/*
 * Copyright (c) 2019 Grzegorz Solinski. All rights reserved.
 */

package com.gpsolinski.remitnow.domain.impl.transactions

import com.gpsolinski.remitnow.domain.Account
import com.gpsolinski.remitnow.domain.Transaction
import com.gpsolinski.remitnow.domain.TransactionState
import com.gpsolinski.remitnow.domain.impl.TransactionType
import com.gpsolinski.remitnow.exceptions.InsufficientFundsException
import lombok.Getter

@Getter
class FakeTransaction implements Transaction {

  final Long id
  BigDecimal amount
  Account creditAccount
  Account debitAccount
  TransactionState state
  TransactionType type

  FakeTransaction(Long id, BigDecimal amount, Account creditAccount, Account debitAccount, TransactionType type) {
    this.id = id
    this.amount = amount
    this.creditAccount = creditAccount
    this.debitAccount = debitAccount
    this.state = TransactionState.NEW
    this.type = type
  }

  @Override
  void complete() throws InsufficientFundsException {

  }
}
