package com.mizanwise.commons.elasticsearch.core.query.restriction;

import com.mizanwise.commons.elasticsearch.core.query.SingleFieldSearchCriterion;
import com.mizanwise.commons.elasticsearch.core.query.exception.InvalidSearchQueryException;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;

import java.util.Map;

public class ElasticFieldRangeRestriction<T> extends SingleFieldSearchCriterion {

    private final T lowerBound;
    private final T upperBound;
    private final Boolean lowerEqual;
    private final Boolean upperEqual;

    public ElasticFieldRangeRestriction(String fieldName, T lowerBound, T upperBound, Boolean lowerEqual,
                                        Boolean upperEqual, Boolean isFilter, float score) throws InvalidSearchQueryException {
        super(fieldName, isFilter, score);
        if (lowerBound == null && upperBound == null)
            throw new InvalidSearchQueryException("both lowerBound and upperBound can not be NULL");

        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.lowerEqual = lowerEqual;
        this.upperEqual = upperEqual;
    }

    @Override
    protected Map<String, Object> toStringData() {
        Map<String, Object> data = super.toStringData();
        data.put("value_Low", this.lowerBound);
        data.put("value_High", this.upperBound);
        data.put("lower_equal", this.lowerEqual);
        data.put("upper_equal", this.upperEqual);
        return data;
    }

    @Override
    public QueryBuilder buildQuery() {
        RangeQueryBuilder query = new RangeQueryBuilder(fieldName);
        if (lowerBound != null)
            if (lowerEqual)
                query.gte(lowerBound);
            else
                query.gt(lowerBound);
        if (upperBound != null)
            if (upperEqual)
                query.lte(upperBound);
            else
                query.lt(upperBound);
        return query;
    }

}
