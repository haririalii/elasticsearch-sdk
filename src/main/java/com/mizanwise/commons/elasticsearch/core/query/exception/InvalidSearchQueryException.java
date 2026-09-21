package com.mizanwise.commons.elasticsearch.core.query.exception;

/**
 * Indicates that an Elasticsearch query is incomplete or invalid.
 */
public class InvalidSearchQueryException extends IllegalArgumentException {
    public InvalidSearchQueryException(String message) {
        super(message);
    }
}
