package com.mizanwise.commons.elasticsearch.core.query.restriction;

import com.mizanwise.commons.elasticsearch.core.query.SingleFieldSearchCriterion;
import com.mizanwise.commons.elasticsearch.core.query.exception.InvalidSearchQueryException;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.Map;

import static com.mizanwise.commons.elasticsearch.core.utils.query.ElasticQueryBuilders.termQuery_keyword;

public class ElasticSingleFieldValueRestriction<T> extends SingleFieldSearchCriterion {

    private final T value;
    private RestrictionOperator operator;

    protected ElasticSingleFieldValueRestriction(T val) throws InvalidSearchQueryException {
        super(null, null, 0);
        this.value = val;
    }

    public ElasticSingleFieldValueRestriction(String fieldName, RestrictionOperator operator, T value, Boolean isFilter,
                                              float queryScore) throws InvalidSearchQueryException {
        super(fieldName, isFilter, queryScore);
        if (StringUtils.isBlank(fieldName) || value == null || operator == null)
            throw new InvalidSearchQueryException("Value or propInfo or operator can not be null");

        this.operator = operator;
        this.value = value;
    }

    @Override
    protected Map<String, Object> toStringData() {
        Map<String, Object> data = super.toStringData();
        data.put("operator", this.operator);
        data.put("value", this.value);
        return data;
    }

    @Override
    public QueryBuilder buildQuery() throws InvalidSearchQueryException {
        switch (operator) {
            case EQ:
                return termQuery_keyword(fieldName, value);
            case GE:
                return QueryBuilders.rangeQuery(fieldName).gte(value);
            case GT:
                return QueryBuilders.rangeQuery(fieldName).gt(value);
            case LE:
                return QueryBuilders.rangeQuery(fieldName).lte(value);
            case LT:
                return QueryBuilders.rangeQuery(fieldName).lt(value);
            case NE:
                return QueryBuilders.boolQuery().mustNot(termQuery_keyword(fieldName, value));
            default:
                throw new InvalidSearchQueryException("Restriction Operator is NULL");
        }
    }

}
