package com.mizanwise.commons.elasticsearch.core.query.restriction;

import com.mizanwise.commons.elasticsearch.core.query.SingleFieldSearchCriterion;
import com.mizanwise.commons.elasticsearch.core.query.exception.InvalidSearchQueryException;
import com.mizanwise.commons.elasticsearch.core.utils.query.ElasticQueryBuilders;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.QueryBuilder;

import java.util.Map;

public class ElasticLikeQueryRestriction extends SingleFieldSearchCriterion {

    private final String value;

    public ElasticLikeQueryRestriction(String field, String value) throws InvalidSearchQueryException {
        this(field, value, false, 1.0F);
    }

    public ElasticLikeQueryRestriction(String fieldName, String value, Boolean isFilter, float score) throws InvalidSearchQueryException {
        super(fieldName, isFilter, score);
        if (StringUtils.isAnyBlank(fieldName, value))
            throw new InvalidSearchQueryException("field and value are required!");

        this.value = value;
    }

    @Override
    protected Map<String, Object> toStringData() {
        Map<String, Object> data = super.toStringData();
        data.put("value", this.value);
        return data;
    }

    @Override
    public QueryBuilder buildQuery() throws InvalidSearchQueryException {
        return ElasticQueryBuilders.likeQuery(fieldName, value);
    }

}
