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
import com.gpsolinski.remitnow.web.dto.ErrorPayload;
import com.gpsolinski.remitnow.web.dto.TransactionPayload;
import com.gpsolinski.remitnow.web.handlers.TransferApiHandler;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.api.OperationRequest;
import io.vertx.ext.web.api.OperationResponse;
import io.vertx.ext.web.api.validation.ValidationException;
import lombok.val;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Currency;
import java.util.Optional;
import java.util.stream.Collectors;

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
        handleSuccess(resultHandler, OperationResponse.completedWithJson(
                        new JsonArray(accounts.stream()
                                .map(account -> new AccountPayload(account).toJson())
                                .collect(Collectors.toList())
                        )
                )
        );
    }

    @Override
    public void createAccount(AccountPayload body, OperationRequest context, Handler<AsyncResult<OperationResponse>> resultHandler) {
        try {
            Currency currency = Currency.getInstance(body.getCurrency());
            Account createdAccount = accountRepository.createDebit(currency);
            handleSuccess(resultHandler, OperationResponse.completedWithJson(
                    new AccountPayload(createdAccount).toJson()).setStatusCode(201)
            );
        } catch (IllegalArgumentException e) {
            handleSuccess(resultHandler, new OperationResponse()
                    .setStatusCode(400)
                    .setStatusMessage("Bad request")
                    .setPayload(Buffer.buffer(
                            new ErrorPayload(
                                    "body.currency",
                                    "Currency code was not recognized",
                                    ValidationException.ErrorType.JSON_INVALID)
                                    .toJson()
                                    .encodePrettily()
                            )
                    )
            );
        }
    }

    @Override
    public void findAccountById(Long accountId, OperationRequest context, Handler<AsyncResult<OperationResponse>> resultHandler) {
        Optional<Account> maybeAccount = accountRepository.findById(accountId);
        if (maybeAccount.isPresent()) {
            handleSuccess(resultHandler, OperationResponse.completedWithJson(
                    maybeAccount.map(account -> new AccountPayload(account).toJson()).get()
            ));
        } else {
            handleSuccess(resultHandler, new OperationResponse()
                    .setStatusCode(404)
                    .setStatusMessage("Not found")
                    .setPayload(Buffer.buffer(
                            new ErrorPayload("Account with the given ID does not exist")
                                    .toJson()
                                    .encodePrettily()
                            )
                    )
            );
        }
    }

    @Override
    public void deposit(Long accountId, AmountPayload body, OperationRequest context, Handler<AsyncResult<OperationResponse>> resultHandler) {
        Optional<Account> maybeAccount = accountRepository.findById(accountId);
        if (maybeAccount.isPresent()) {
            val account = maybeAccount.get();
            val amount = new BigDecimal(body.getAmount());
            val deposit = transactionRepository.createDeposit(account, amount);
            deposit.complete();
            transactionRepository.save(deposit);
            handleSuccess(resultHandler, OperationResponse.completedWithJson(
                    new AccountPayload(account).toJson())
            );

        } else {
            handleSuccess(resultHandler, new OperationResponse()
                    .setStatusCode(404)
                    .setStatusMessage("Not found")
                    .setPayload(Buffer.buffer(
                            new ErrorPayload("Account with the given ID does not exist")
                                    .toJson()
                                    .encodePrettily()
                            )
                    )
            );
        }
    }

    @Override
    public void withdraw(Long accountId, AmountPayload body, OperationRequest context, Handler<AsyncResult<OperationResponse>> resultHandler) {
        Optional<Account> maybeAccount = accountRepository.findById(accountId);
        if (maybeAccount.isPresent()) {
            val account = maybeAccount.get();
            val amount = new BigDecimal(body.getAmount());
            val withdrawal = transactionRepository.createWithdrawal(account, amount);
            try {
                withdrawal.complete();
                transactionRepository.save(withdrawal);
                handleSuccess(resultHandler, OperationResponse.completedWithJson(
                        new AccountPayload(account).toJson())
                );
            } catch (InsufficientFundsException e) {
                handleSuccess(resultHandler, new OperationResponse()
                                        .setStatusCode(400)
                                        .setStatusMessage("Bad request")
                                        .setPayload(Buffer.buffer(
                                                new ErrorPayload("Insufficient balance in the given account")
                                                        .toJson()
                                                        .encodePrettily()
                                                )
                                        )
                );
            }
        } else {
            handleSuccess(resultHandler, new OperationResponse()
                    .setStatusCode(404)
                    .setStatusMessage("Not found")
                    .setPayload(Buffer.buffer(
                            new ErrorPayload("Account with the given ID does not exist")
                                    .toJson()
                                    .encodePrettily()
                            )
                    )
            );
        }
    }

    @Override
    public void getTransactions(OperationRequest context, Handler<AsyncResult<OperationResponse>> resultHandler) {
        Collection<Transaction> transactions = transactionRepository.getAll();
        handleSuccess(resultHandler, OperationResponse.completedWithJson(
                new JsonArray(transactions.stream()
                        .map(tr -> new TransactionPayload(tr).toJson())
                        .collect(Collectors.toList())
                )
                )
        );
    }

    @Override
    public void findTransactionById(Long transactionId, OperationRequest context, Handler<AsyncResult<OperationResponse>> resultHandler) {
        Optional<Transaction> maybeTransaction = transactionRepository.findById(transactionId);
        if (maybeTransaction.isPresent()) {
            handleSuccess(resultHandler, OperationResponse.completedWithJson(
                    maybeTransaction.map(transaction -> new TransactionPayload(transaction).toJson()).get()
                    )
            );
        } else {
            handleSuccess(resultHandler, new OperationResponse()
                    .setStatusCode(404)
                    .setStatusMessage("Not found")
                    .setPayload(Buffer.buffer(
                            new ErrorPayload("Transaction with the given ID does not exist")
                                    .toJson()
                                    .encodePrettily()
                            )
                    )
            );
        }
    }

    @Override
    public void transfer(TransactionPayload body, OperationRequest context, Handler<AsyncResult<OperationResponse>> resultHandler) {
        BigDecimal amount = new BigDecimal(body.getAmount());
        Long senderAccountId = body.getFromAccount();
        Long recipientAccountId = body.getToAccount();
        Transaction transfer = null;
        try {
            Account senderAccount = accountRepository.findById(senderAccountId).orElseThrow(() -> accountNotFound(senderAccountId));
            Account recipientAccount = accountRepository.findById(recipientAccountId).orElseThrow(() -> accountNotFound(recipientAccountId));
            transfer = transferService.transferFunds(senderAccount, recipientAccount, amount);
        } catch (AccountNotFoundException e) {
            handleSuccess(resultHandler, new OperationResponse()
                    .setStatusCode(404)
                    .setStatusMessage("Not found")
                    .setPayload(Buffer.buffer(
                            new ErrorPayload(e.getMessage())
                                    .toJson()
                                    .encodePrettily()
                            )
                    )
            );
        } catch (InsufficientFundsException e) {
            handleSuccess(resultHandler, new OperationResponse()
                    .setStatusCode(400)
                    .setStatusMessage("Bad request")
                    .setPayload(Buffer.buffer(
                            new ErrorPayload("Insufficient balance in the account to credit")
                                    .toJson()
                                    .encodePrettily()
                            )
                    )
            );
        }
        handleSuccess(resultHandler, OperationResponse.completedWithJson(new TransactionPayload(transfer).toJson()));
    }

    private void handleSuccess(Handler<AsyncResult<OperationResponse>> handler, OperationResponse response) {
        handler.handle(Future.succeededFuture(response));
    }

    private AccountNotFoundException accountNotFound(Long accountId) {
        return new AccountNotFoundException(accountId);
    }
}
