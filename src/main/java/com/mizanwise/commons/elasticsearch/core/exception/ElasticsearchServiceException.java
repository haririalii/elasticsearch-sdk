package com.mizanwise.commons.elasticsearch.core.exception;

public class ElasticsearchServiceException extends RuntimeException {

    public ElasticsearchServiceException(Throwable cause) {
        super(cause);
    }

    public ElasticsearchServiceException(String message) {
        super(message);
    }
}
