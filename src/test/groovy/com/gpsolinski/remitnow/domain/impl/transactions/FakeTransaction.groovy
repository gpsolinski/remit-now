package com.gpsolinski.remitnow.domain.impl.transactions

import com.gpsolinski.remitnow.domain.Account
import com.gpsolinski.remitnow.domain.Transaction
import com.gpsolinski.remitnow.domain.TransactionState
import com.gpsolinski.remitnow.exceptions.InsufficientFundsException
import lombok.Getter

@Getter
class FakeTransaction implements Transaction {

  final Long id
  BigDecimal amount
  Account creditAccount
  Account debitAccount
  TransactionState state

  FakeTransaction(Long id, BigDecimal amount, Account creditAccount, Account debitAccount) {
    this.id = id
    this.amount = amount
    this.creditAccount = creditAccount
    this.debitAccount = debitAccount
    this.state = TransactionState.NEW
  }

  @Override
  void complete() throws InsufficientFundsException {

  }
}
