package services.impl

import domain.BankAccount
import exceptions.InsufficientFundsException

class SimpleBankAccount implements BankAccount<Long> {
  Long ID
  BigDecimal balance = 0
  Currency currency = Currency.getInstance(Locale.US)

  @Override
  void deposit(BigDecimal amount) {
    balance = balance.add(amount)
  }

  @Override
  void withdraw(BigDecimal amount) throws InsufficientFundsException {
    if (balance < amount) {
      throw new InsufficientFundsException()
    }
    balance = balance.subtract(amount)
  }
}
