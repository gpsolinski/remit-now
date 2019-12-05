/*
 * Copyright (c) 2019 Grzegorz Solinski. All rights reserved.
 */

package com.gpsolinski.remitnow.repository;

import com.gpsolinski.remitnow.domain.Account;

import java.util.Currency;

public interface AccountRepository extends BasicRepository<Account, Long> {
    Account createDebit(Currency currency);
}
