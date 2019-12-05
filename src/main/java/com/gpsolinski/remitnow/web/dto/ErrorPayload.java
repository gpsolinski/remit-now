package com.gpsolinski.remitnow.web.dto;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.validation.ValidationException;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@DataObject(generateConverter = true, publicConverter = false)
public class ErrorPayload {
    private String property;
    private String message;
    private ValidationException.ErrorType errorType;

    public ErrorPayload(JsonObject json) {
        ErrorPayloadConverter.fromJson(json, this);
    }

    public ErrorPayload(String message) {
        this.message = message;
    }

    public ErrorPayload(ValidationException exception) {
        this.property = exception.parameterName();
        this.message = exception.getMessage();
        this.errorType = exception.type();
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        ErrorPayloadConverter.toJson(this, json);
        return json;
    }

    public String getProperty() {
        return property;
    }

    @Fluent
    public ErrorPayload setProperty(String property) {
        this.property = property;
        return this;
    }

    public String getMessage() {
        return message;
    }

    @Fluent
    public ErrorPayload setMessage(String message) {
        this.message = message;
        return this;
    }

    public ValidationException.ErrorType getErrorType() {
        return errorType;
    }

    @Fluent
    public ErrorPayload setErrorType(ValidationException.ErrorType errorType) {
        this.errorType = errorType;
        return this;
    }
}
