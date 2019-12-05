/*
 * Copyright (c) 2019 Grzegorz Solinski. All rights reserved.
 */

package com.gpsolinski.remitnow.validators;

import java.math.BigDecimal;

public class TransactionAmountValidator {

    public static void validate(BigDecimal amount) {
        validatePositive(amount);
    }

    private static void validatePositive(BigDecimal amount) {
        if (BigDecimal.ZERO.compareTo(amount) >= 0) {
            throw new IllegalArgumentException("Transaction amount needs to be positive");
        }
    }
}
