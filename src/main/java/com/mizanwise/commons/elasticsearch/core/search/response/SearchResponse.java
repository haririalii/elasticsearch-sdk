package com.mizanwise.commons.elasticsearch.core.search.response;

import com.mizanwise.commons.elasticsearch.core.mapping.ElasticsearchMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SearchResponse<E extends ElasticsearchMapping<?>> {

    private List<SearchItem<E>> items;
    private String scrollId;

    public SearchResponse() {
        this.items = new ArrayList<>();
    }

    public SearchResponse(List<SearchItem<E>> items, String scrollId) {
        this.items = items;
        this.scrollId = scrollId;
    }

    public List<E> getEntities() {
        return this.getItems()
                .stream()
                .map(SearchItem::getSource)
                .collect(Collectors.toList());
    }

    public List<SearchItem<E>> getItems() {
        return items;
    }

    public void setItems(List<SearchItem<E>> items) {
        this.items = items;
    }

    public String getScrollId() {
        return scrollId;
    }

    public void setScrollId(String scrollId) {
        this.scrollId = scrollId;
    }

}
