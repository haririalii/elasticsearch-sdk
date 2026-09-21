package com.mizanwise.commons.elasticsearch.core.query;

import com.mizanwise.commons.elasticsearch.core.query.exception.InvalidSearchQueryException;
import org.elasticsearch.index.query.QueryBuilder;

/**
 * A criterion that can contribute a query clause to an Elasticsearch request.
 */
public interface ElasticSearchCriterion {

    /**
     * Builds the native Elasticsearch query clause.
     */
    QueryBuilder buildQuery() throws InvalidSearchQueryException;

    /**
     * Indicates whether this criterion must be emitted as a filter clause.
     */
    Boolean isFilter();

    /**
     * Returns the boost applied to this criterion.
     */
    float getBoostValue();

}
