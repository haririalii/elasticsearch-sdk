package com.mizanwise.commons.elasticsearch.core.search.response;

/**
 * Elasticsearch search highlight response entity
 */
public class Highlight {

    private String fieldName;
    private String fragments;

    public Highlight(String fieldName, String fragments) {
        this.fieldName = fieldName;
        this.fragments = fragments;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFragments() {
        return fragments;
    }

    public void setFragments(String fragments) {
        this.fragments = fragments;
    }

    @Override
    public String toString() {
        return "Highlight{" +
                "fieldName='" + fieldName + '\'' +
                ", fragments='" + fragments + '\'' +
                '}';
    }
}
