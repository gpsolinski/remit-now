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
import com.gpsolinski.remitnow.services.TransactionService;
import com.gpsolinski.remitnow.web.dto.AccountPayload;
import com.gpsolinski.remitnow.web.dto.AmountPayload;
import com.gpsolinski.remitnow.web.dto.TransactionPayload;
import com.gpsolinski.remitnow.web.handlers.TransferApiHandler;
import com.gpsolinski.remitnow.web.util.JsonUtil;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.ext.web.api.OperationRequest;
import io.vertx.ext.web.api.OperationResponse;
import io.vertx.ext.web.api.validation.ValidationException;
import lombok.val;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Currency;
import java.util.Optional;

import static com.gpsolinski.remitnow.web.util.OperationHandlerUtil.*;

public class DefaultTransferApiHandler implements TransferApiHandler {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionService transactionService;

    public DefaultTransferApiHandler(AccountRepository accountRepository,
                                     TransactionRepository transactionRepository,
                                     TransactionService transactionService) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.transactionService = transactionService;
    }

    @Override
    public void getAccounts(OperationRequest context, Handler<AsyncResult<OperationResponse>> resultHandler) {
        Collection<Account> accounts = accountRepository.getAll();
        handleOk(resultHandler, JsonUtil.transformAccounts(accounts));
    }

    @Override
    public void createAccount(AccountPayload body, OperationRequest context, Handler<AsyncResult<OperationResponse>> resultHandler) {
        try {
            Currency currency = Currency.getInstance(body.getCurrency());
            Account createdAccount = accountRepository.createDebit(currency);
            handleCreated(resultHandler, JsonUtil.transformAccount(createdAccount));
        } catch (IllegalArgumentException e) {
            handleBadRequest(resultHandler, JsonUtil.createValidationError(
                    "Currency code was not recognized",
                    "currency",
                    ValidationException.ErrorType.JSON_INVALID));
        }
    }

    @Override
    public void findAccountById(Long accountId, OperationRequest context, Handler<AsyncResult<OperationResponse>> resultHandler) {
        Optional<Account> maybeAccount = accountRepository.findById(accountId);
        try {
            handleOk(resultHandler, maybeAccount.map(JsonUtil::transformAccount)
                            .orElseThrow(() -> new AccountNotFoundException(accountId)));
        } catch (AccountNotFoundException e) {
            handleNotFound(resultHandler, JsonUtil.transformError(e));
        }
    }

    @Override
    public void deposit(Long accountId, AmountPayload body, OperationRequest context, Handler<AsyncResult<OperationResponse>> resultHandler) {
        Optional<Account> maybeAccount = accountRepository.findById(accountId);
        try {
            val account = maybeAccount.orElseThrow(() -> new AccountNotFoundException(accountId));
            val amount = new BigDecimal(body.getAmount());
            transactionService.depositFunds(account, amount);
            handleOk(resultHandler, JsonUtil.transformAccount(account));
        } catch (AccountNotFoundException e) {
            handleNotFound(resultHandler, JsonUtil.transformError(e));
        }
    }

    @Override
    public void withdraw(Long accountId, AmountPayload body, OperationRequest context, Handler<AsyncResult<OperationResponse>> resultHandler) {
        Optional<Account> maybeAccount = accountRepository.findById(accountId);
        try {
            val account = maybeAccount.orElseThrow(() -> new AccountNotFoundException(accountId));
            val amount = new BigDecimal(body.getAmount());
            transactionService.withdrawFunds(account, amount);
            handleOk(resultHandler, JsonUtil.transformAccount(account));
        } catch (AccountNotFoundException e) {
            handleNotFound(resultHandler, JsonUtil.transformError(e));
        } catch (InsufficientFundsException e) {
            handleBadRequest(resultHandler, JsonUtil.transformError(e));
        }
    }

    @Override
    public void getTransactions(OperationRequest context, Handler<AsyncResult<OperationResponse>> resultHandler) {
        Collection<Transaction> transactions = transactionRepository.getAll();
        handleOk(resultHandler, JsonUtil.transformTransactions(transactions));
    }

    @Override
    public void findTransactionById(Long transactionId, OperationRequest context, Handler<AsyncResult<OperationResponse>> resultHandler) {
        Optional<Transaction> maybeTransaction = transactionRepository.findById(transactionId);
        if (maybeTransaction.isPresent()) {
            handleOk(resultHandler, maybeTransaction.map(JsonUtil::transformTransaction).get());
        } else {
            handleNotFound(resultHandler, JsonUtil.createError("Transaction with the given ID does not exist"));
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
            transfer = transactionService.transferFunds(senderAccount, recipientAccount, amount);
        } catch (AccountNotFoundException e) {
            handleNotFound(resultHandler, JsonUtil.transformError(e));
        } catch (InsufficientFundsException e) {
            handleBadRequest(resultHandler, JsonUtil.transformError(e));
        }
        handleOk(resultHandler, JsonUtil.transformTransaction(transfer));
    }

    private AccountNotFoundException accountNotFound(Long accountId) {
        return new AccountNotFoundException(accountId);
    }
}
