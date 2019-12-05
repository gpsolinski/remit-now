/*
 * Copyright (c) 2019 Grzegorz Solinski. All rights reserved.
 */

package com.gpsolinski.remitnow.web.handlers;

import com.gpsolinski.remitnow.repository.AccountRepository;
import com.gpsolinski.remitnow.repository.TransactionRepository;
import com.gpsolinski.remitnow.services.TransferService;
import com.gpsolinski.remitnow.web.dto.AccountPayload;
import com.gpsolinski.remitnow.web.dto.AmountPayload;
import com.gpsolinski.remitnow.web.dto.TransactionPayload;
import com.gpsolinski.remitnow.web.handlers.impl.DefaultTransferApiHandler;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.ext.web.api.OperationRequest;
import io.vertx.ext.web.api.OperationResponse;
import io.vertx.ext.web.api.generator.WebApiServiceGen;

@WebApiServiceGen
public interface TransferApiHandler {

    static TransferApiHandler create(AccountRepository accountRepository,
                                     TransactionRepository transactionRepository,
                                     TransferService transferService) {
        return new DefaultTransferApiHandler(accountRepository, transactionRepository, transferService);
    }

    void getAccounts(OperationRequest context, Handler<AsyncResult<OperationResponse>> resultHandler);
    void createAccount(AccountPayload body, OperationRequest context, Handler<AsyncResult<OperationResponse>> resultHandler);
    void findAccountById(Long accountId, OperationRequest context, Handler<AsyncResult<OperationResponse>> resultHandler);
    void deposit(Long accountId, AmountPayload body, OperationRequest context, Handler<AsyncResult<OperationResponse>> resultHandler);
    void withdraw(Long accountId, AmountPayload body, OperationRequest context, Handler<AsyncResult<OperationResponse>> resultHandler);
    void getTransactions(OperationRequest context, Handler<AsyncResult<OperationResponse>> resultHandler);
    void findTransactionById(Long transactionId, OperationRequest context, Handler<AsyncResult<OperationResponse>> resultHandler);
    void transfer(TransactionPayload body, OperationRequest context, Handler<AsyncResult<OperationResponse>> resultHandler);
}
