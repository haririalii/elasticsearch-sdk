package com.mizanwise.commons.elasticsearch.core.query.restriction;

import com.mizanwise.commons.elasticsearch.core.query.SingleFieldSearchCriterion;
import com.mizanwise.commons.elasticsearch.core.query.exception.InvalidSearchQueryException;
import com.mizanwise.commons.elasticsearch.core.utils.query.ElasticQueryBuilders;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.QueryBuilder;

public class ElasticRegexQueryRestriction extends SingleFieldSearchCriterion {

    private final String regex;

    public ElasticRegexQueryRestriction(String field, String regex) throws InvalidSearchQueryException {
        this(field, regex, false, 1);
    }

    public ElasticRegexQueryRestriction(String field, String regex, Boolean isFilter, float boostValue)
            throws InvalidSearchQueryException {
        super(field, isFilter, boostValue);
        if (StringUtils.isAnyBlank(field, regex))
            throw new InvalidSearchQueryException("field and regex are required!");

        this.regex = regex;
    }

    @Override
    public QueryBuilder buildQuery() throws InvalidSearchQueryException {
        return ElasticQueryBuilders.regexpQuery(fieldName, regex);
    }

}
