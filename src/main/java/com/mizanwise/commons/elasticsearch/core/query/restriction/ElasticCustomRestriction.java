package com.mizanwise.commons.elasticsearch.core.query.restriction;

import com.mizanwise.commons.elasticsearch.core.query.AbstractElasticSearchCriterion;
import com.mizanwise.commons.elasticsearch.core.query.exception.InvalidSearchQueryException;
import org.elasticsearch.index.query.QueryBuilder;

public class ElasticCustomRestriction extends AbstractElasticSearchCriterion {

    private final QueryBuilder queryBuilder;

    public ElasticCustomRestriction(QueryBuilder queryBuilder, Boolean isFilter, float boostValue) {
        super(isFilter, boostValue);
        if (queryBuilder == null)
            throw new IllegalArgumentException("QueryBuilder cannot be null!");

        this.queryBuilder = queryBuilder;
    }

    @Override
    public void addPrefixFieldName(String prefixFieldName) {

    }

    @Override
    public QueryBuilder buildQuery() throws InvalidSearchQueryException {
        return this.queryBuilder;
    }
}
