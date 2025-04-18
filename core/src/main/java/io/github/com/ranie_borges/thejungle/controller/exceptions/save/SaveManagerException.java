package io.github.com.ranie_borges.thejungle.controller.exceptions.save;

public class SaveManagerException extends RuntimeException {
    public SaveManagerException(String message) {
        super(message);
    }

    public SaveManagerException(String message, Throwable cause) {
        super(message, cause);
    }
}
