package com.mizanwise.commons.elasticsearch.core.index;

import com.mizanwise.commons.elasticsearch.core.mapping.ElasticsearchMapping;
import co.elastic.clients.elasticsearch._types.Refresh;

/**
 * this class was build for execute index request on the elasticsearch
 */
public class ElasticIndexRequest<E extends ElasticsearchMapping<?>> {

    private String id;
    private E data;
    private Refresh refreshPolicy;

    public ElasticIndexRequest(String id) {
        this.id(id);
    }

    public ElasticIndexRequest(String id, E data) {
        this(id);
        this.data(data);
    }

    public ElasticIndexRequest(String id, E data, Refresh refreshPolicy) {
        this(id, data);
        this.refreshPolicy(refreshPolicy);
    }

    public boolean validateForAddOrUpdate() {
        return this.id() != null && this.data() != null;
    }

    public boolean validateForDelete() {
        return this.id() != null;
    }

    public ElasticIndexRequest<E> id(String id) {
        this.id = id;

        return this;
    }

    public ElasticIndexRequest<E> data(E data) {
        this.data = data;

        return this;
    }

    public ElasticIndexRequest<E> refreshPolicy(Refresh refreshPolicy) {
        this.refreshPolicy = refreshPolicy;

        return this;
    }

    public String id() {
        return id;
    }

    public E data() {
        return data;
    }

    public Refresh refreshPolicy() {
        return refreshPolicy;
    }

}
