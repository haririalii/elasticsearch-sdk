package com.mizanwise.commons.elasticsearch.core.index.response;

import co.elastic.clients.elasticsearch._types.Result;

/**
 * index response entity
 */
public class IndexResponse {

    private final String index;
    private final String id;
    private final Result elasticResult;
    private final long version;
    private final ElasticsearchState state;

    public IndexResponse(String index, String id, Result elasticResult, long version) {
        this.index = index;
        this.id = id;
        this.elasticResult = elasticResult;
        this.version = version;
        this.state = this.convertResult(elasticResult);
    }

    public IndexResponse(co.elastic.clients.elasticsearch.core.IndexResponse response) {
        this(response.index(), response.id(), response.result(), response.version());
    }

    public IndexResponse(co.elastic.clients.elasticsearch.core.DeleteResponse response) {
        this(response.index(), response.id(), response.result(), response.version());
    }

    private ElasticsearchState convertResult(Result result) {
        switch (result) {
            case Created:
            case Updated:
                return ElasticsearchState.INDEXED;
            case Deleted:
                return ElasticsearchState.DELETED;
            default:
                return ElasticsearchState.INDEXING_FAILED;
        }
    }

    public String getIndex() {
        return index;
    }

    public String getId() {
        return id;
    }

    public Result getElasticResult() {
        return elasticResult;
    }

    public long getVersion() {
        return version;
    }

    public ElasticsearchState getState() {
        return state;
    }

    @Override
    public String toString() {
        return "IndexResponse{" +
                "index='" + index + '\'' +
                ", id='" + id + '\'' +
                ", result=" + elasticResult +
                ", version=" + version +
                '}';
    }
}
