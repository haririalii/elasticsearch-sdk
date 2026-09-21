package com.mizanwise.commons.elasticsearch.core.query.restriction;

import com.mizanwise.commons.elasticsearch.core.query.AbstractElasticSearchCriterion;
import com.mizanwise.commons.elasticsearch.core.query.exception.InvalidSearchQueryException;
import org.apache.commons.collections4.CollectionUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.Collection;
import java.util.Map;

public class ElasticCompositeRestriction extends AbstractElasticSearchCriterion {

    private final QueryComposition composition;
    private final Collection<AbstractElasticSearchCriterion> criterions;
    private final Integer minimumShouldMatch;

    public ElasticCompositeRestriction(Collection<AbstractElasticSearchCriterion> criterions, QueryComposition composition,
                                       boolean isFilter, float boostValue) throws InvalidSearchQueryException {
        this(criterions, composition, null, isFilter, boostValue);
    }

    public ElasticCompositeRestriction(Collection<AbstractElasticSearchCriterion> criterions, QueryComposition composition,
                                       Integer minimumShouldMatch, boolean isFilter, float boostValue) throws InvalidSearchQueryException {
        super(isFilter, boostValue);
        if (CollectionUtils.isEmpty(criterions) || composition == null)
            throw new InvalidSearchQueryException("QueryComposition or criterions is not valid, Composition"
                    + composition + ",Criterions: " + criterions);

        this.criterions = criterions;
        this.composition = composition;
        this.minimumShouldMatch = minimumShouldMatch;
    }

    @Override
    public void addPrefixFieldName(String prefixFieldName) {
        for (AbstractElasticSearchCriterion query : criterions)
            query.addPrefixFieldName(prefixFieldName);
    }

    @Override
    protected Map<String, Object> toStringData() {
        Map<String, Object> data = super.toStringData();
        data.put("composition", this.composition.toString());
        data.put("criterion", this.criterions.toString());
        data.put("minimumShouldMatch", this.minimumShouldMatch);
        return data;
    }

    @Override
    public QueryBuilder buildQuery() throws InvalidSearchQueryException {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        switch (composition) {
            case AND:
                for (AbstractElasticSearchCriterion query : criterions)
                    boolQuery.must(query.buildQuery());
                break;
            case OR:
                for (AbstractElasticSearchCriterion query : criterions)
                    boolQuery.should(query.buildQuery());
                break;
            case NOT:
                for (AbstractElasticSearchCriterion query : criterions)
                    boolQuery.mustNot(query.buildQuery());
                break;
            default:
                throw new InvalidSearchQueryException("QueryComposition is not valid" + composition);
        }

        if (this.minimumShouldMatch != null && CollectionUtils.isNotEmpty(boolQuery.should()))
            boolQuery.minimumShouldMatch(minimumShouldMatch);

        return boolQuery;
    }

}
