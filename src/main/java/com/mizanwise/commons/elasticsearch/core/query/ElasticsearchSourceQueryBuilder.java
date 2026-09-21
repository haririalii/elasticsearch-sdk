package com.mizanwise.commons.elasticsearch.core.query;

import com.mizanwise.commons.elasticsearch.core.query.exception.InvalidSearchQueryException;
import com.mizanwise.commons.elasticsearch.core.query.model.SortDirection;
import com.mizanwise.commons.elasticsearch.core.query.restriction.QueryComposition;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;

import java.util.List;
import java.util.Objects;

/**
 * Builds Elasticsearch {@link SearchSourceBuilder} instances from the library query model.
 */
public final class ElasticsearchSourceQueryBuilder {

    private ElasticsearchSourceQueryBuilder() {
    }

    /**
     * Builds a request without pagination.
     */
    public static SearchSourceBuilder buildSearchRequest(ElasticSearchQuery query) throws InvalidSearchQueryException {
        return buildSearchRequest(query, null, null);
    }

    public static SearchSourceBuilder buildSearchRequest(ElasticSearchQuery query, Integer from, Integer size)
            throws InvalidSearchQueryException {
        Objects.requireNonNull(query, "query");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        if (from != null)
            sourceBuilder.from(from);
        if (size != null)
            sourceBuilder.size(size);
        sourceBuilder.highlighter(buildHighlighter(query));
        sourceBuilder.query(buildQueries(query));
        List<AggregationBuilder> aggs = buildAggregations(query);
        for (AggregationBuilder agg : aggs) {
            sourceBuilder.aggregation(agg);
        }
        sourceBuilder.sort(buildSorts(query));
        return sourceBuilder;
    }

    public static List<SortBuilder<?>> buildSorts(ElasticSearchQuery query) throws InvalidSearchQueryException {
        return query.getSortEntries()
                .stream()
                .<SortBuilder<?>>map(order -> new FieldSortBuilder(order.field())
                        .order(
                                getSortOrder(order.direction())
                        ))
                .toList();
    }

    private static SortOrder getSortOrder(SortDirection orderDirection) throws InvalidSearchQueryException {
        switch (orderDirection) {
            case ASCENDING:
                return SortOrder.ASC;
            case DESCENDING:
                return SortOrder.DESC;
            default:
                throw new InvalidSearchQueryException("sort order is not valid: " + orderDirection);
        }
    }

    public static BoolQueryBuilder buildQueries(ElasticSearchQuery elasticSearchQuery) throws InvalidSearchQueryException {
        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        List<ElasticSearchCriterion> criterias = List.copyOf(elasticSearchQuery.getCriteria());
        QueryComposition nonFilterComposition = elasticSearchQuery.getNonFilterComposition();

        for (ElasticSearchCriterion criteria : criterias) {
            if (criteria.isFilter()) {
                builder.filter(criteria.buildQuery());
                continue;
            }
            switch (nonFilterComposition) {
                case AND:
                    builder.must(criteria.buildQuery());
                    break;
                case OR:
                    builder.should(criteria.buildQuery());
                    builder.minimumShouldMatch(1);
                    break;
                case NOT:
                    builder.mustNot(criteria.buildQuery());
                    break;
                default:
                    throw new InvalidSearchQueryException(
                            "noneFilter composition is not valid " + nonFilterComposition);
            }
        }

/*        if (CollectionUtils.isEmpty(builder.must()))
            builder.must(QueryBuilders.matchAllQuery());*/

        return builder;
    }

    private static List<AggregationBuilder> buildAggregations(ElasticSearchQuery elasticSearchQuery) {
        return elasticSearchQuery.getAggregations()
                .stream()
                .<AggregationBuilder>map(ElasticSearchAggregation::build)
                .toList();
    }

    private static HighlightBuilder buildHighlighter(ElasticSearchQuery query) {
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        for (ElasticSearchHighlightEntry highlightField : query.getHighlightFields())
            highlightBuilder.field(highlightField.getFieldName(), highlightField.getFragmentSize(),
                    highlightField.getFragmentNumber());

        highlightBuilder.preTags(query.getHighlighterPreTag());
        highlightBuilder.postTags(query.getHighlighterPostTag());
        if (query.getNoMatchSize() > 0)
            highlightBuilder.noMatchSize(query.getNoMatchSize());

        return highlightBuilder;
    }
}
