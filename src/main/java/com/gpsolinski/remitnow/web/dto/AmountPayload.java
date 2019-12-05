/*
 * Copyright (c) 2019 Grzegorz Solinski. All rights reserved.
 */

package com.gpsolinski.remitnow.web.dto;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@DataObject(generateConverter = true, publicConverter = false)
public class AmountPayload {

    private String amount;

    public AmountPayload(JsonObject json) {
        AmountPayloadConverter.fromJson(json, this);
    }

    public AmountPayload(BigDecimal amount) {
        this.amount = amount.toPlainString();
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        AmountPayloadConverter.toJson(this, json);
        return json;
    }

    public String getAmount() {
        return amount;
    }

    @Fluent
    public AmountPayload setAmount(String amount) {
        this.amount = amount;
        return this;
    }
}
