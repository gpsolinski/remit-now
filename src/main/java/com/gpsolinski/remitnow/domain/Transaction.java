/*
 * Copyright (c) 2019 Grzegorz Solinski. All rights reserved.
 */

package com.gpsolinski.remitnow.domain;

import com.gpsolinski.remitnow.domain.impl.TransactionType;
import com.gpsolinski.remitnow.exceptions.InsufficientFundsException;

import java.math.BigDecimal;

public interface Transaction extends Identifiable<Long> {
    Account getCreditAccount();
    Account getDebitAccount();
    BigDecimal getAmount();
    TransactionState getState();
    TransactionType getType();
    void complete() throws InsufficientFundsException;
}
