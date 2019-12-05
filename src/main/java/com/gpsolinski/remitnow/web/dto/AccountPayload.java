/*
 * Copyright (c) 2019 Grzegorz Solinski. All rights reserved.
 */

package com.gpsolinski.remitnow.web.dto;

import com.gpsolinski.remitnow.domain.Account;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@DataObject(generateConverter = true, publicConverter = false)
public class AccountPayload {
    private Long id;
    private String currency;
    private String balance;
    private String availableBalance;

    public AccountPayload(JsonObject json) {
        AccountPayloadConverter.fromJson(json, this);
    }

    public AccountPayload(Account account) {
        this.id = account.getId();
        this.currency = account.getCurrency().getCurrencyCode();
        this.balance = account.getBalance().setScale(2).toPlainString();
        this.availableBalance = account.getAvailableBalance().setScale(2).toPlainString();
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        AccountPayloadConverter.toJson(this, json);
        return json;
    }

    public Long getId() {
        return id;
    }

    public String getCurrency() {
        return currency;
    }

    public String getBalance() {
        return balance;
    }

    public String getAvailableBalance() {
        return availableBalance;
    }

    @Fluent
    public AccountPayload setId(Long id) {
        this.id = id;
        return this;
    }

    @Fluent
    public AccountPayload setCurrency(String currency) {
        this.currency = currency;
        return this;
    }

    @Fluent
    public AccountPayload setBalance(String balance) {
        this.balance = balance;
        return this;
    }

    @Fluent
    public AccountPayload setAvailableBalance(String availableBalance) {
        this.availableBalance = availableBalance;
        return this;
    }
}
