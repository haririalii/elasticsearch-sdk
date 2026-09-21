package com.mizanwise.commons.elasticsearch.core.search.response;

import com.mizanwise.commons.elasticsearch.core.mapping.ElasticsearchMapping;

import java.util.List;

/**
 * Elasticsearch search response item entity
 */
public class SearchItem<E extends ElasticsearchMapping<?>> {

    private String id;
    private List<Highlight> highlights;
    private E source;

    public SearchItem() {
    }

    public SearchItem(String id) {
        this.id = id;
    }

    public SearchItem(String id, List<Highlight> highlights, E source) {
        this.id = id;
        this.highlights = highlights;
        this.source = source;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Highlight> getHighlights() {
        return highlights;
    }

    public void setHighlights(List<Highlight> highlights) {
        this.highlights = highlights;
    }

    public E getSource() {
        return source;
    }

    public void setSource(E source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return "SearchItem{" +
                "id='" + id + '\'' +
                ", highlights=" + highlights +
                ", source=" + source +
                '}';
    }
}
