package com.mizanwise.commons.elasticsearch.core.mapping;

public class MappingException extends RuntimeException {

    public MappingException(Throwable cause) {
        super(cause);
    }

    public MappingException(String message) {
        super(message);
    }
}
