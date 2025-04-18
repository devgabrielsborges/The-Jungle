package io.github.com.ranie_borges.thejungle.controller.exceptions.ambient;

public class ResourceOperationException extends AmbientControllerException {
    public ResourceOperationException(String message) {
        super(message);
    }

    public ResourceOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
