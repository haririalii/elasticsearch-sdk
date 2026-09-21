package com.mizanwise.commons.elasticsearch.core.dao.utils;

import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import com.mizanwise.commons.elasticsearch.core.mapping.ElasticsearchMapping;
import com.mizanwise.commons.elasticsearch.core.search.response.Highlight;
import com.mizanwise.commons.elasticsearch.core.search.response.SearchItem;
import com.mizanwise.commons.elasticsearch.core.search.response.SearchResponse;
import com.mizanwise.commons.elasticsearch.core.utils.JsonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Converts Java API client search responses into this library's response model. */
public final class SearchRequestUtils {

    private SearchRequestUtils() {
    }

    public static <E extends ElasticsearchMapping<?>> SearchResponse<E> getResponse(
            co.elastic.clients.elasticsearch.core.SearchResponse<JsonData> searchResponse, Class<E> mappingClass) {
        return getResponse(searchResponse.hits().hits(), searchResponse.scrollId(), mappingClass);
    }

    public static <E extends ElasticsearchMapping<?>> SearchResponse<E> getResponse(
            co.elastic.clients.elasticsearch.core.ScrollResponse<JsonData> searchResponse, Class<E> mappingClass) {
        return getResponse(searchResponse.hits().hits(), searchResponse.scrollId(), mappingClass);
    }

    private static <E extends ElasticsearchMapping<?>> SearchResponse<E> getResponse(
            List<Hit<JsonData>> hits, String scrollId, Class<E> mappingClass) {
        List<SearchItem<E>> items = hits.stream()
                .map(hit -> new SearchItem<>(hit.id(), getHighlights(hit), getSource(hit, mappingClass)))
                .toList();
        return new SearchResponse<>(items, scrollId);
    }

    private static <E> E getSource(Hit<JsonData> hit, Class<E> mappingClass) {
        return hit.source() == null ? null : JsonUtils.convert(hit.source().toJson(), mappingClass);
    }

    private static List<Highlight> getHighlights(Hit<JsonData> hit) {
        List<Highlight> highlights = new ArrayList<>();
        for (Map.Entry<String, List<String>> field : hit.highlight().entrySet()) {
            highlights.add(new Highlight(field.getKey(), field.getValue().toString()));
        }
        return highlights;
    }
}
