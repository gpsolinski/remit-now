/*
 * Copyright (c) 2019 Grzegorz Solinski. All rights reserved.
 */

package com.gpsolinski.remitnow.web;

import com.gpsolinski.remitnow.context.ApplicationContext;
import com.gpsolinski.remitnow.web.dto.ErrorPayload;
import com.gpsolinski.remitnow.web.handlers.TransferApiHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.contract.openapi3.OpenAPI3RouterFactory;
import io.vertx.ext.web.api.validation.ValidationException;
import io.vertx.serviceproxy.ServiceBinder;
import lombok.val;

public class TransferServiceMainVerticle extends AbstractVerticle {

    private HttpServer server;
    private MessageConsumer<JsonObject> consumer;

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new TransferServiceMainVerticle());
    }

    @Override
    public void start(Future<Void> future) {
        startTransferService();
        startHttpServer().setHandler(future);
    }

    private void startTransferService() {
        val serviceBinder = new ServiceBinder(vertx);

        val context = ApplicationContext.create();

        val accountRepository = context.getAccountRepository();
        val transactionRepository = context.getTransactionRepository();
        val transferService = context.getTransferService();

        // Create an instance of TransactionManagerService and mount to event bus
        TransferApiHandler transferApiHandler = TransferApiHandler.create(accountRepository,
                transactionRepository, transferService);
        consumer = serviceBinder
                .setAddress("transfer_api.gpsolinski.com")
                .register(TransferApiHandler.class, transferApiHandler);
    }

    /**
     * This method constructs the router factory, mounts services and handlers and starts the http server with built router
     * @return
     */
    private Future<Void> startHttpServer() {
        Promise<Void> promise = Promise.promise();
        OpenAPI3RouterFactory.create(this.vertx, "/transfer_api-1.0.yaml", openAPI3RouterFactoryAsyncResult -> {
            if (openAPI3RouterFactoryAsyncResult.succeeded()) {
                val routerFactory = openAPI3RouterFactoryAsyncResult.result();

                // Mount services on event bus based on extensions
                routerFactory.mountServicesFromExtensions();

                // Generate the router
                val router = routerFactory.getRouter();

                // Manage the validation failure for all routes in the router
                router.errorHandler(400, routingContext -> {
                    val response = routingContext.response()
                            .setStatusCode(400)
                            .setStatusMessage("Bad request")
                            .setChunked(true);
                    val cause = routingContext.failure();
                    if (cause instanceof ValidationException) {
                        val errorPayload = new ErrorPayload((ValidationException)cause);
                        response.write(errorPayload.toJson().encodePrettily());
                    }
                    response.end();
                });
                server = vertx.createHttpServer(new HttpServerOptions().setPort(8080).setHost("localhost"));
                server.requestHandler(router).listen(result -> {
                    if (result.succeeded())
                        promise.complete();
                    else
                        promise.fail(result.cause());
                });
            } else {
                // Something went wrong during router factory initialization
                promise.fail(openAPI3RouterFactoryAsyncResult.cause());
            }
        });
        return promise.future();
    }

    /**
     * This method closes the http server and unregister all services loaded to Event Bus
     */
    @Override
    public void stop(){
        this.server.close();
        consumer.unregister();
    }
}
