/*
 * Copyright (c) 2019 Grzegorz Solinski. All rights reserved.
 */

package com.gpsolinski.remitnow.web.util;

import com.gpsolinski.remitnow.domain.Account;
import com.gpsolinski.remitnow.domain.Transaction;
import com.gpsolinski.remitnow.web.dto.AccountPayload;
import com.gpsolinski.remitnow.web.dto.ErrorPayload;
import com.gpsolinski.remitnow.web.dto.TransactionPayload;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.validation.ValidationException;

import java.util.Collection;
import java.util.stream.Collectors;

public class JsonUtil {

    public static JsonObject transformAccount(Account account) {
        return new AccountPayload(account).toJson();
    }

    public static JsonArray transformAccounts(Collection<Account> accounts) {
        return new JsonArray(accounts.stream()
                .map(account -> new AccountPayload(account).toJson())
                .collect(Collectors.toList())
        );
    }

    public static JsonObject transformTransaction(Transaction transaction) {
        return new TransactionPayload(transaction).toJson();
    }

    public static JsonArray transformTransactions(Collection<Transaction> transactions) {
        return new JsonArray(transactions.stream()
                .map(transaction -> new TransactionPayload(transaction).toJson())
                .collect(Collectors.toList())
        );
    }

    public static JsonObject transformError(Throwable throwable) {
        return new ErrorPayload(throwable.getMessage()).toJson();
    }

    public static JsonObject createError(String message) {
        return new ErrorPayload(message).toJson();
    }

    public static JsonObject createValidationError(String message, String property, ValidationException.ErrorType type) {
        return new ErrorPayload(message, property, type).toJson();
    }
}
