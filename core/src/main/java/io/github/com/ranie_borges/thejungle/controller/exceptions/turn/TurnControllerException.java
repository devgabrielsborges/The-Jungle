package io.github.com.ranie_borges.thejungle.controller.exceptions.turn;

public class TurnControllerException extends RuntimeException {
    public TurnControllerException(String message) {
        super(message);
    }

    public TurnControllerException(String message, Throwable cause) {
        super(message, cause);
    }
}
