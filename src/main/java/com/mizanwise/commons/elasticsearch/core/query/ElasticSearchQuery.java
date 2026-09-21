package com.mizanwise.commons.elasticsearch.core.query;

import com.mizanwise.commons.elasticsearch.core.query.model.SortEntry;
import com.mizanwise.commons.elasticsearch.core.query.restriction.QueryComposition;

import java.util.ArrayList;
import java.util.Collection;

public class ElasticSearchQuery {

    private final Collection<ElasticSearchCriterion> criteria;
    private final Collection<SortEntry> sortEntries;
    private String highlighterPreTag;
    private String highlighterPostTag;
    private int noMatchSize;
    private Collection<String> indices;
    private Collection<ElasticSearchHighlightEntry> highlightFields;
    private Collection<ElasticSearchAggregation> aggregations;
    private QueryComposition nonFilterComposition;

    private ElasticSearchQuery() {
        indices = new ArrayList<>();
        highlightFields = new ArrayList<>();
        aggregations = new ArrayList<>();
        criteria = new ArrayList<>();
        sortEntries = new ArrayList<>();
        nonFilterComposition = QueryComposition.AND;
        highlighterPreTag = "<em>";
        highlighterPostTag = "</em>";
        noMatchSize = 0;
    }

    public ElasticSearchQuery(Collection<String> indices) {
        this();
        this.indices = indices;
    }

    public ElasticSearchQuery(String index, QueryComposition nonFilterComposition) {
        this();
        this.indices.add(index);
        this.nonFilterComposition = nonFilterComposition;
    }

    public ElasticSearchQuery(Collection<String> indices, QueryComposition nonFilterComposition) {
        this();
        this.indices = indices;
        this.nonFilterComposition = nonFilterComposition;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder res = new StringBuilder(this.getClass().getSimpleName() + "{" + "criteria: [\n");
        for (ElasticSearchCriterion searchCriterion : criteria) {
            res.append("\t").append(searchCriterion).append("\n");
        }
        res.append("], orderEntries: [\n");
        for (SortEntry orderEntry : sortEntries) {
            res.append("\t").append(orderEntry).append("\n");
        }
        res.append("]}");
        return res.toString();
    }

    public ElasticSearchQuery addCriterion(ElasticSearchCriterion criterion) {
        criteria.add(criterion);
        return this;
    }

    public ElasticSearchQuery addSort(SortEntry sortEntry) {
        sortEntries.add(sortEntry);
        return this;
    }

    public Collection<ElasticSearchCriterion> getCriteria() {
        return criteria;
    }

    public Collection<SortEntry> getSortEntries() {
        return sortEntries;
    }

    public void addAggregation(ElasticSearchAggregation agg) {
        if (aggregations == null) {
            aggregations = new ArrayList<>();
        }
        aggregations.add(agg);
    }

    public ElasticSearchQuery setHighterlightTags(String preTag, String postTag) {
        this.highlighterPreTag = preTag;
        this.highlighterPostTag = postTag;
        return this;
    }

    public String getHighlighterPreTag() {
        return highlighterPreTag;
    }

    public void setHighlighterPreTag(String highlighterPreTag) {
        this.highlighterPreTag = highlighterPreTag;
    }

    public String getHighlighterPostTag() {
        return highlighterPostTag;
    }

    public void setHighlighterPostTag(String highlighterPostTag) {
        this.highlighterPostTag = highlighterPostTag;
    }

    public int getNoMatchSize() {
        return noMatchSize;
    }

    public void setNoMatchSize(int noMatchSize) {
        this.noMatchSize = noMatchSize;
    }

    public Collection<String> getIndices() {
        return indices;
    }

    public void setIndices(Collection<String> indices) {
        this.indices = indices;
    }

    public Collection<ElasticSearchHighlightEntry> getHighlightFields() {
        return highlightFields;
    }

    public void setHighlightFields(Collection<ElasticSearchHighlightEntry> highlightFields) {
        this.highlightFields = highlightFields;
    }

    public Collection<ElasticSearchAggregation> getAggregations() {
        return aggregations;
    }

    public void setAggregations(Collection<ElasticSearchAggregation> aggregations) {
        this.aggregations = aggregations;
    }

    public QueryComposition getNonFilterComposition() {
        return nonFilterComposition;
    }

    public void setNonFilterComposition(QueryComposition nonFilterComposition) {
        this.nonFilterComposition = nonFilterComposition;
    }
}
