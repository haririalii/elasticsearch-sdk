package com.mizanwise.commons.elasticsearch.core.query.model;

import java.util.Objects;

/**
 * A field and direction used to order search results.
 */
public record SortEntry(String field, SortDirection direction) {
    public SortEntry {
        Objects.requireNonNull(field, "field");
        Objects.requireNonNull(direction, "direction");
    }
}
