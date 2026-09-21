package com.mizanwise.commons.elasticsearch.core.query;

import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.range.Range;
import org.elasticsearch.search.aggregations.bucket.range.RangeAggregationBuilder;

import java.util.ArrayList;

public class ElasticSearchRangeAggregation implements ElasticSearchAggregation {

    private final String name;
    private final ArrayList<ElasticSearchRange> ranges;
    private final String fieldName;

    public ElasticSearchRangeAggregation(String fieldName, ArrayList<ElasticSearchRange> ranges) {
        super();
        this.fieldName = fieldName;
        this.name = generateName();
        this.ranges = ranges;
    }

    public ElasticSearchRangeAggregation(String fieldName, String name, ArrayList<ElasticSearchRange> ranges) {
        super();
        this.fieldName = fieldName;
        this.name = name;
        this.ranges = ranges;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public RangeAggregationBuilder build() {
        RangeAggregationBuilder agg = AggregationBuilders.range(name);
        agg.field(fieldName);
        for (ElasticSearchRange range : ranges) {
            if (range.hasTo())
                agg.addUnboundedTo(range.getTo());
            else if (range.hasFrom())
                agg.addUnboundedFrom(range.getFrom());
            else if (range.hasAll())
                agg.addRange(range.getFrom(), range.getTo());

        }

        return agg;
    }

    private String generateName() {
        return fieldName + "_count_by_range";
    }

    @Override
    public ArrayList<AggregationResultItem> enrichResponse(Aggregation aggs) {
        Range agg = (Range) aggs;
        ArrayList<AggregationResultItem> items = new ArrayList<>();
        for (Range.Bucket entry : agg.getBuckets()) {
            String key = entry.getKeyAsString();
            long docCount = entry.getDocCount();
            items.add(new AggregationResultItem(key, docCount));
        }
        return items;
    }

}
