package com.mizanwise.commons.elasticsearch.core.query.restriction;

import com.mizanwise.commons.elasticsearch.core.query.SingleFieldSearchCriterion;
import com.mizanwise.commons.elasticsearch.core.query.exception.InvalidSearchQueryException;
import org.apache.commons.collections4.CollectionUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.Collection;
import java.util.Map;

public class ElasticCollectionIntersectRestriction<T> extends SingleFieldSearchCriterion {

    private final int minMatchRequired;
    private final Collection<T> collection;

    public ElasticCollectionIntersectRestriction(String fieldName, Collection<T> collection, int minMatchRequired,
                                                 Boolean isFilter, float score) throws InvalidSearchQueryException {
        super(fieldName, isFilter, score);
        if (CollectionUtils.isEmpty(collection))
            throw new InvalidSearchQueryException("Collection can not be empty");

        this.collection = collection;
        this.minMatchRequired = minMatchRequired;
    }

    @Override
    protected Map<String, Object> toStringData() {
        Map<String, Object> data = super.toStringData();
        data.put("collection", this.collection);
        data.put("minMatchRequired", this.minMatchRequired);

        return data;
    }

    @Override
    public QueryBuilder buildQuery() throws InvalidSearchQueryException {
        BoolQueryBuilder baseQuery = QueryBuilders.boolQuery();
        for (T val : collection)
            baseQuery.should(QueryBuilders.termQuery(fieldName, val));

        baseQuery.minimumShouldMatch(minMatchRequired);

        return baseQuery;

    }

}
