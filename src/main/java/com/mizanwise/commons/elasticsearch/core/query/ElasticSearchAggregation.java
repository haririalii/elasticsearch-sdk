package com.mizanwise.commons.elasticsearch.core.query;

import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.Aggregation;

import java.util.List;

/**
 * Defines an aggregation and maps its native Elasticsearch response into library result items.
 */
public interface ElasticSearchAggregation {

    /**
     * Returns the aggregation name used in the response.
     */
    String getName();

    /**
     * Builds the corresponding native aggregation.
     */
    AbstractAggregationBuilder<?> build();

    /**
     * Converts a native aggregation response to named result items.
     */
    List<AggregationResultItem> enrichResponse(Aggregation agg);
}
