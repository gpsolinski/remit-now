/*
 * Copyright (c) 2019 Grzegorz Solinski. All rights reserved.
 */

package com.gpsolinski.remitnow.web.handlers.impl;

import com.gpsolinski.remitnow.domain.Account;
import com.gpsolinski.remitnow.domain.Transaction;
import com.gpsolinski.remitnow.exceptions.AccountNotFoundException;
import com.gpsolinski.remitnow.exceptions.InsufficientFundsException;
import com.gpsolinski.remitnow.repository.AccountRepository;
import com.gpsolinski.remitnow.repository.TransactionRepository;
import com.gpsolinski.remitnow.services.TransferService;
import com.gpsolinski.remitnow.web.dto.AccountPayload;
import com.gpsolinski.remitnow.web.dto.AmountPayload;
import com.gpsolinski.remitnow.web.dto.TransactionPayload;
import com.gpsolinski.remitnow.web.handlers.TransferApiHandler;
import com.gpsolinski.remitnow.web.util.JsonUtils;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.OperationRequest;
import io.vertx.ext.web.api.OperationResponse;
import io.vertx.ext.web.api.validation.ValidationException;
import lombok.val;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Currency;
import java.util.Optional;

public class DefaultTransferApiHandler implements TransferApiHandler {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final TransferService transferService;

    public DefaultTransferApiHandler(AccountRepository accountRepository,
                                     TransactionRepository transactionRepository,
                                     TransferService transferService) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.transferService = transferService;
    }

    @Override
    public void getAccounts(OperationRequest context, Handler<AsyncResult<OperationResponse>> resultHandler) {
        Collection<Account> accounts = accountRepository.getAll();
        handleOk(resultHandler, JsonUtils.transformAccounts(accounts));
    }

    @Override
    public void createAccount(AccountPayload body, OperationRequest context, Handler<AsyncResult<OperationResponse>> resultHandler) {
        try {
            Currency currency = Currency.getInstance(body.getCurrency());
            Account createdAccount = accountRepository.createDebit(currency);
            handleCreated(resultHandler, JsonUtils.transformAccount(createdAccount));
        } catch (IllegalArgumentException e) {
            handleBadRequest(resultHandler, JsonUtils.createValidationError(
                    "Currency code was not recognized",
                    "currency",
                    ValidationException.ErrorType.JSON_INVALID));
        }
    }

    @Override
    public void findAccountById(Long accountId, OperationRequest context, Handler<AsyncResult<OperationResponse>> resultHandler) {
        Optional<Account> maybeAccount = accountRepository.findById(accountId);
        try {
            handleOk(resultHandler, maybeAccount.map(JsonUtils::transformAccount)
                            .orElseThrow(() -> new AccountNotFoundException(accountId)));
        } catch (AccountNotFoundException e) {
            handleNotFound(resultHandler, JsonUtils.transformError(e));
        }
    }

    @Override
    public void deposit(Long accountId, AmountPayload body, OperationRequest context, Handler<AsyncResult<OperationResponse>> resultHandler) {
        Optional<Account> maybeAccount = accountRepository.findById(accountId);
        try {
            val account = maybeAccount.orElseThrow(() -> new AccountNotFoundException(accountId));
            val amount = new BigDecimal(body.getAmount());
            val deposit = transactionRepository.createDeposit(account, amount);
            deposit.complete();
            transactionRepository.save(deposit);
            handleOk(resultHandler, JsonUtils.transformAccount(account));
        } catch (AccountNotFoundException e) {
            handleNotFound(resultHandler, JsonUtils.transformError(e));
        }
    }

    @Override
    public void withdraw(Long accountId, AmountPayload body, OperationRequest context, Handler<AsyncResult<OperationResponse>> resultHandler) {
        Optional<Account> maybeAccount = accountRepository.findById(accountId);
        try {
            val account = maybeAccount.orElseThrow(() -> new AccountNotFoundException(accountId));
            val amount = new BigDecimal(body.getAmount());
            val withdrawal = transactionRepository.createWithdrawal(account, amount);
            withdrawal.complete();
            transactionRepository.save(withdrawal);
            handleOk(resultHandler, JsonUtils.transformAccount(account));
        } catch (AccountNotFoundException e) {
            handleNotFound(resultHandler, JsonUtils.transformError(e));
        } catch (InsufficientFundsException e) {
            handleBadRequest(resultHandler, JsonUtils.transformError(e));
        }
    }

    @Override
    public void getTransactions(OperationRequest context, Handler<AsyncResult<OperationResponse>> resultHandler) {
        Collection<Transaction> transactions = transactionRepository.getAll();
        handleOk(resultHandler, JsonUtils.transformTransactions(transactions));
    }

    @Override
    public void findTransactionById(Long transactionId, OperationRequest context, Handler<AsyncResult<OperationResponse>> resultHandler) {
        Optional<Transaction> maybeTransaction = transactionRepository.findById(transactionId);
        if (maybeTransaction.isPresent()) {
            handleOk(resultHandler, maybeTransaction.map(JsonUtils::transformTransaction).get());
        } else {
            handleNotFound(resultHandler, JsonUtils.createError("Transaction with the given ID does not exist"));
        }
    }

    @Override
    public void transfer(TransactionPayload body, OperationRequest context, Handler<AsyncResult<OperationResponse>> resultHandler) {
        val amount = new BigDecimal(body.getAmount());
        val senderAccountId = body.getFromAccount();
        val recipientAccountId = body.getToAccount();
        Transaction transfer = null;
        try {
            val senderAccount = accountRepository.findById(senderAccountId).orElseThrow(() -> accountNotFound(senderAccountId));
            val recipientAccount = accountRepository.findById(recipientAccountId).orElseThrow(() -> accountNotFound(recipientAccountId));
            transfer = transferService.transferFunds(senderAccount, recipientAccount, amount);
        } catch (AccountNotFoundException e) {
            handleNotFound(resultHandler, JsonUtils.transformError(e));
        } catch (InsufficientFundsException e) {
            handleBadRequest(resultHandler, JsonUtils.transformError(e));
        }
        handleSuccess(resultHandler, OperationResponse.completedWithJson(JsonUtils.transformTransaction(transfer)));
    }

    private void handleOk(Handler<AsyncResult<OperationResponse>> handler, JsonObject jsonObject) {
        handleSuccess(handler, OperationResponse.completedWithJson(jsonObject));
    }

    private void handleOk(Handler<AsyncResult<OperationResponse>> handler, JsonArray jsonArray) {
        handleSuccess(handler, OperationResponse.completedWithJson(jsonArray));
    }

    private void handleCreated(Handler<AsyncResult<OperationResponse>> handler, JsonObject jsonObject) {
        handleSuccess(handler, OperationResponse.completedWithJson(jsonObject)
                .setStatusCode(201)
                .setStatusMessage("Created"));
    }

    private void handleNotFound(Handler<AsyncResult<OperationResponse>> handler, JsonObject errorPayload) {
        handleSuccess(handler, new OperationResponse()
                .setStatusCode(404)
                .setStatusMessage("Not Found")
                .setPayload(errorPayload.toBuffer()));
    }

    private void handleBadRequest(Handler<AsyncResult<OperationResponse>> handler, JsonObject errorPayload) {
        handleSuccess(handler, new OperationResponse()
                .setStatusCode(400)
                .setStatusMessage("Bad Request")
                .setPayload(errorPayload.toBuffer()));
    }

    private void handleSuccess(Handler<AsyncResult<OperationResponse>> handler, OperationResponse response) {
        handler.handle(Future.succeededFuture(response));
    }

    private AccountNotFoundException accountNotFound(Long accountId) {
        return new AccountNotFoundException(accountId);
    }
}
