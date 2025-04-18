package io.github.com.ranie_borges.thejungle.controller.exceptions.ambient;

public class AmbientControllerException extends RuntimeException {
    public AmbientControllerException(String message) {
        super(message);
    }

    public AmbientControllerException(String message, Throwable cause) {
        super(message, cause);
    }
}
