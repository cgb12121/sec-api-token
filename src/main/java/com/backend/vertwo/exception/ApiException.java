package com.backend.vertwo.exception;

public class ApiException extends RuntimeException {
    public ApiException(String message) {
        super("ApiException: " + message);
    }

    public ApiException() {
        super("An error occurred!");
    }
}
