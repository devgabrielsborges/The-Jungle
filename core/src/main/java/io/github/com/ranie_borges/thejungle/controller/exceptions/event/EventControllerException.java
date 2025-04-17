package io.github.com.ranie_borges.thejungle.controller.exceptions.event;

public class EventControllerException extends RuntimeException {
    public EventControllerException(String message) {
        super(message);
    }

    public EventControllerException(String message, Throwable cause) {
        super(message, cause);
    }
}
