package com.mizanwise.commons.elasticsearch.core.index.response;

public enum ElasticsearchState {
    DIRTY,
    IN_QUEUE,
    INDEXING_FAILED,
    INDEXED,
    DELETED
}
