package com.mizanwise.commons.elasticsearch.core.query.restriction;

import com.mizanwise.commons.elasticsearch.core.query.SingleFieldSearchCriterion;
import com.mizanwise.commons.elasticsearch.core.query.exception.InvalidSearchQueryException;
import com.mizanwise.commons.elasticsearch.core.utils.query.ElasticQueryBuilders;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.QueryBuilder;

import java.util.Map;

public class ElasticWildcardQueryRestriction extends SingleFieldSearchCriterion {

    private final String wildcard;

    public ElasticWildcardQueryRestriction(String field, String wildcard) throws InvalidSearchQueryException {
        this(field, wildcard, false, 1.0F);
    }

    public ElasticWildcardQueryRestriction(String fieldName, String wildcard, Boolean isFilter, float score) throws InvalidSearchQueryException {
        super(fieldName, isFilter, score);
        if (StringUtils.isAnyBlank(fieldName, wildcard))
            throw new InvalidSearchQueryException("field and wildcard are required!");

        this.wildcard = wildcard;
    }

    @Override
    protected Map<String, Object> toStringData() {
        Map<String, Object> data = super.toStringData();
        data.put("value", this.wildcard);
        return data;
    }

    @Override
    public QueryBuilder buildQuery() throws InvalidSearchQueryException {
        return ElasticQueryBuilders.keywordExistenceQuery(
                this.fieldName,
                field -> ElasticQueryBuilders.wildcardQuery(field, this.wildcard)
        );
    }

}
