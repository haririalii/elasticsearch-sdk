package com.mizanwise.commons.elasticsearch.core.query.restriction;

import com.mizanwise.commons.elasticsearch.core.query.AbstractElasticSearchCriterion;
import com.mizanwise.commons.elasticsearch.core.query.exception.InvalidSearchQueryException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.DisMaxQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import static com.mizanwise.commons.elasticsearch.core.utils.query.ElasticQueryBuilders.fullTextQuery;

public class ElasticFullTextQueryRestriction extends AbstractElasticSearchCriterion {

    private static final Logger logger = LoggerFactory.getLogger(ElasticFullTextQueryRestriction.class);
    private final Boolean allFields;
    private final String value;
    private Collection<String> fields;
    private String analyzer;

    public ElasticFullTextQueryRestriction(String value, Boolean isFilter, float score) throws InvalidSearchQueryException {
        super(isFilter, score);
        if (StringUtils.isBlank(value))
            throw new InvalidSearchQueryException("Value can not be null");

        this.value = value;
        this.allFields = true;
    }

    public ElasticFullTextQueryRestriction(String field, String value, Boolean isFilter, float score)
            throws InvalidSearchQueryException {
        super(isFilter, score);
        if (StringUtils.isAnyBlank(field, value))
            throw new InvalidSearchQueryException("Field or value can not be null");

        this.value = value;
        this.allFields = false;
        fields = new ArrayList<>();
        fields.add(field);
    }

    public ElasticFullTextQueryRestriction(Collection<String> fields, String value, Boolean isFilter, float score)
            throws InvalidSearchQueryException {
        super(isFilter, score);
        if (StringUtils.isBlank(value) || CollectionUtils.isEmpty(fields))
            throw new InvalidSearchQueryException("Value or fields can not be null");

        this.fields = fields;
        this.value = value;
        this.allFields = false;
    }

    public ElasticFullTextQueryRestriction analyzer(String analyzer) {
        this.analyzer = analyzer;
        return this;
    }

    @Override
    public void addPrefixFieldName(String prefixFieldName) {
        if (allFields) {
            logger.error("Adding prefix field name for 'allFields' in {} is meaningless!'",
                    getClass().getSimpleName());
            return;
        }

        this.fields = fields.stream()
                .map(field -> prefixFieldName + "." + field)
                .collect(Collectors.toList());
    }

    @Override
    protected Map<String, Object> toStringData() {
        Map<String, Object> data = super.toStringData();
        data.put("value", this.value);
        data.put("allFields", this.allFields);
        data.put("fields", this.fields);

        return data;
    }

    @Override
    public QueryBuilder buildQuery() throws InvalidSearchQueryException {
        if (allFields) {
            return allFieldQuery(this.value, this.analyzer);
        } else if (this.fields.size() == 1) {
            String propInfo = this.fields.iterator().next();
            return fullTextQuery(propInfo, this.value, this.analyzer);
        } else {
            DisMaxQueryBuilder dm = QueryBuilders.disMaxQuery();
            for (String f : this.fields)
                if (StringUtils.isNotBlank((f)))
                    dm.add(fullTextQuery(f, this.value, this.analyzer));

            return dm;
        }
    }

    private QueryBuilder allFieldQuery(String value, String analyzer) {
        QueryStringQueryBuilder query = QueryBuilders.queryStringQuery(value);
        if (analyzer != null)
            query.analyzer(analyzer);

        return query;
    }

}
