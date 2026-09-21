package com.mizanwise.commons.elasticsearch.core.exception;

public class InsufficientFiledException extends RuntimeException {

    private String field;

    public InsufficientFiledException() {
    }

    public InsufficientFiledException(String field) {
        super(field + " is required but is not provided!");
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
