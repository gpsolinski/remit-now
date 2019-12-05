package com.gpsolinski.remitnow.web.dto;

import com.gpsolinski.remitnow.domain.Transaction;
import com.gpsolinski.remitnow.domain.TransactionState;
import com.gpsolinski.remitnow.domain.impl.TransactionType;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.core.json.JsonObject;
import lombok.Data;

@Data
@DataObject(generateConverter = true, publicConverter = false)
public class TransactionPayload {
    private Long id;
    private Long fromAccount;
    private Long toAccount;
    private String amount;
    private TransactionState state;
    private TransactionType type;

    public TransactionPayload(JsonObject json) {
        TransactionPayloadConverter.fromJson(json, this);
    }

    public TransactionPayload(Transaction transaction) {
        this.id = transaction.getId();
        if (hasCreditAccount(transaction)) {
            this.fromAccount = transaction.getCreditAccount().getId();
        }
        if (hasDebitAccount(transaction)) {
            this.toAccount = transaction.getDebitAccount().getId();
        }
        this.amount = transaction.getAmount().toPlainString();
        this.state = transaction.getState();
        this.type = transaction.getType();
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        TransactionPayloadConverter.toJson(this, json);
        return json;
    }

    private boolean hasCreditAccount(Transaction transaction) {
        return transaction.getType() != TransactionType.DEPOSIT;
    }

    private boolean hasDebitAccount(Transaction transaction) {
        return transaction.getType() != TransactionType.WITHDRAWAL;
    }

    public Long getId() {
        return id;
    }

    @Fluent
    public TransactionPayload setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getFromAccount() {
        return fromAccount;
    }

    @Fluent
    public TransactionPayload setFromAccount(Long fromAccount) {
        this.fromAccount = fromAccount;
        return this;
    }

    public Long getToAccount() {
        return toAccount;
    }

    @Fluent
    public TransactionPayload setToAccount(Long toAccount) {
        this.toAccount = toAccount;
        return this;
    }

    public String getAmount() {
        return amount;
    }

    @Fluent
    public TransactionPayload setAmount(String amount) {
        this.amount = amount;
        return this;
    }

    public TransactionState getState() {
        return state;
    }

    @Fluent
    public TransactionPayload setState(TransactionState state) {
        this.state = state;
        return this;
    }

    public TransactionType getType() {
        return type;
    }

    @Fluent
    public TransactionPayload setType(TransactionType type) {
        this.type = type;
        return this;
    }
}
