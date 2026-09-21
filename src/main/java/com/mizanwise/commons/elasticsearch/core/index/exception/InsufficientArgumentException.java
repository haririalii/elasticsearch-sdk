package com.mizanwise.commons.elasticsearch.core.index.exception;

public class InsufficientArgumentException extends RuntimeException {
    public InsufficientArgumentException() {
    }

    public InsufficientArgumentException(String message) {
        super(message);
    }
}
