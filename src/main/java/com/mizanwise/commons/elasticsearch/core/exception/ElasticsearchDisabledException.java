package com.mizanwise.commons.elasticsearch.core.exception;

public class ElasticsearchDisabledException extends RuntimeException {

    public ElasticsearchDisabledException() {
        super("Elasticsearch service is disabled.");
    }
}
