package com.mizanwise.commons.elasticsearch.core.query;

/**
 * A named aggregation bucket and its document count.
 */
public record AggregationResultItem(String name, long totalItems) {
}
