package com.mizanwise.commons.elasticsearch.core.mapping;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Defines the Elasticsearch representation of a domain entity.
 *
 * @param <E> source domain-entity type
 */
public interface ElasticsearchMapping<E> {
    /**
     * Populates this mapping from its source entity.
     */
    void loadFrom(E entity);

    /**
     * Returns the document identifier used by Elasticsearch.
     */
    @JsonIgnore
    String getId();
}
