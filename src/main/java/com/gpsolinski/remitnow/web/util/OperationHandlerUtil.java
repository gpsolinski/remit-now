/*
 * Copyright (c) 2019 Grzegorz Solinski. All rights reserved.
 */

package com.gpsolinski.remitnow.web.util;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.OperationResponse;

public class OperationHandlerUtil {

    public static void handleOk(Handler<AsyncResult<OperationResponse>> handler, JsonObject jsonObject) {
        handleSuccess(handler, OperationResponse.completedWithJson(jsonObject));
    }

    public static void handleOk(Handler<AsyncResult<OperationResponse>> handler, JsonArray jsonArray) {
        handleSuccess(handler, OperationResponse.completedWithJson(jsonArray));
    }

    public static void handleCreated(Handler<AsyncResult<OperationResponse>> handler, JsonObject jsonObject) {
        handleSuccess(handler, OperationResponse.completedWithJson(jsonObject)
                .setStatusCode(201)
                .setStatusMessage("Created"));
    }

    public static void handleNotFound(Handler<AsyncResult<OperationResponse>> handler, JsonObject errorPayload) {
        handleSuccess(handler, new OperationResponse()
                .setStatusCode(404)
                .setStatusMessage("Not Found")
                .setPayload(errorPayload.toBuffer()));
    }

    public static void handleBadRequest(Handler<AsyncResult<OperationResponse>> handler, JsonObject errorPayload) {
        handleSuccess(handler, new OperationResponse()
                .setStatusCode(400)
                .setStatusMessage("Bad Request")
                .setPayload(errorPayload.toBuffer()));
    }

    private static void handleSuccess(Handler<AsyncResult<OperationResponse>> handler, OperationResponse response) {
        handler.handle(Future.succeededFuture(response));
    }
}
