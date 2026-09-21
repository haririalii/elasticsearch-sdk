package com.mizanwise.commons.elasticsearch.core.query;

import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;

import java.util.ArrayList;

public class ElasticSearchBucketAggregation implements ElasticSearchAggregation {

    private String fieldName;
    private String name;

    public ElasticSearchBucketAggregation(String fieldName) {
        super();
        this.fieldName = fieldName;
        this.name = generateName();
    }

    public ElasticSearchBucketAggregation(String fieldName, String name) {
        super();
        this.fieldName = fieldName;
        this.name = name;
    }

    private String generateName() {
        return fieldName + "_count_by_type";
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public TermsAggregationBuilder build() {
        return AggregationBuilders.terms(getName()).field(fieldName);
    }

    @Override
    public ArrayList<AggregationResultItem> enrichResponse(Aggregation aggs) {
        Terms agg = (Terms) aggs;
        ArrayList<AggregationResultItem> items = new ArrayList<>();
        for (Terms.Bucket b : agg.getBuckets())
            items.add(new AggregationResultItem(b.getKeyAsString(), b.getDocCount()));

        return items;
    }
}
