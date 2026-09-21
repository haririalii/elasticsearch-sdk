package com.mizanwise.commons.elasticsearch.core.search;

import com.mizanwise.commons.elasticsearch.core.query.ElasticSearchQuery;
import com.mizanwise.commons.elasticsearch.core.query.ElasticsearchSourceQueryBuilder;
import com.mizanwise.commons.elasticsearch.core.query.exception.InvalidSearchQueryException;
import com.mizanwise.commons.elasticsearch.core.utils.IndexUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * this class build for execute search request on elasticsearch
 */
public class ElasticSearchRequest {

    private SearchSourceBuilder source;

    private Collection<String> indices;
    private ReturnType returnType = ReturnType.FULL;

    private boolean scrollAble = false;
    private String scrollId;

    public ElasticSearchRequest() {
        this.source = new SearchSourceBuilder();
    }

    public ElasticSearchRequest(ElasticSearchQuery searchQuery) throws InvalidSearchQueryException {
        this.source = ElasticsearchSourceQueryBuilder.buildSearchRequest(searchQuery);
        this.indices(searchQuery.getIndices());
    }

    public ElasticSearchRequest(String index) {
        this();
        this.indices(index);
    }

    public ElasticSearchRequest(SearchSourceBuilder source, Collection<String> indices, ReturnType returnType) {
        this.source = source;
        this.indices(indices);
        this.returnType(returnType);
    }

    public ElasticSearchRequest(SearchSourceBuilder source,
                                List<String> indices,
                                ReturnType returnType,
                                boolean scrollAble,
                                String scrollId) {
        this.source = source;
        this.indices(indices);
        this.returnType(returnType);
        this.scrollAble(scrollAble);
        this.scrollId(scrollId);
    }

    private List<String> appendIndexPrefix(Collection<String> indices) {
        return indices.stream()
                .map(IndexUtils::appendIndexPrefix)
                .collect(Collectors.toList());
    }

    public boolean validateForSearch() {
        return CollectionUtils.isNotEmpty(indices()) && source() != null && returnType() != null;
    }

    public boolean validateForCount() {
        return CollectionUtils.isNotEmpty(indices()) && source() != null;
    }

    /**
     * Specifies search query
     *
     * @param queries search query
     * @return this
     */
    public ElasticSearchRequest queries(QueryBuilder queries) {
        this.source.query(queries);

        return this;
    }

    /**
     * Specifies highlight fields
     *
     * @param highlight HighlightBuilder
     * @return this
     */
    public ElasticSearchRequest highlights(HighlightBuilder highlight) {
        this.source.highlighter(highlight);

        return this;
    }

    /**
     * Specifies sort
     *
     * @param sorts list of the sort items
     * @return this
     */
    public ElasticSearchRequest sorts(List<SortBuilder<?>> sorts) {
        sorts.forEach(source::sort);

        return this;
    }

    /**
     * beginning item of the search
     *
     * @param from an integer
     * @return this
     */
    public ElasticSearchRequest from(Integer from) {
        this.source.from(from);

        return this;
    }

    /**
     * length of the search response
     *
     * @param size an integer
     * @return this
     */
    public ElasticSearchRequest size(Integer size) {
        this.source.size(size);

        return this;
    }

    /**
     * List of indices to search.
     *
     * @param indices list of indices
     * @return this
     */
    public ElasticSearchRequest indices(Collection<String> indices) {
        this.indices = this.appendIndexPrefix(indices);

        return this;
    }

    public ElasticSearchRequest indices(String index) {
        this.indices = this.appendIndexPrefix(Collections.singletonList(index));

        return this;
    }

    /**
     * type of the search response
     *
     * @param returnType a return type
     * @return this
     */
    public ElasticSearchRequest returnType(ReturnType returnType) {
        this.returnType = returnType;

        return this;
    }

    /**
     * in case of we want to use Scroll API
     *
     * @param scrollAble a boolean
     * @return this
     */
    public ElasticSearchRequest scrollAble(boolean scrollAble) {
        this.scrollAble = scrollAble;

        return this;
    }

    /**
     * in case of search with Scroll API
     *
     * @param scrollId a scroll id
     * @return this
     */
    public ElasticSearchRequest scrollId(String scrollId) {
        this.scrollId = scrollId;

        return this;
    }

    /**
     * source of the search
     *
     * @param source source
     * @return this
     */
    public ElasticSearchRequest source(SearchSourceBuilder source) {
        this.source = source;

        return this;
    }

    public Collection<String> indices() {
        return indices;
    }

    public ReturnType returnType() {
        return returnType;
    }

    public boolean scrollAble() {
        return scrollAble;
    }

    public String scrollId() {
        return scrollId;
    }

    public SearchSourceBuilder source() {
        return this.source;
    }
}

