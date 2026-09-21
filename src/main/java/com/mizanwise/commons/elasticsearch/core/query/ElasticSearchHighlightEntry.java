package com.mizanwise.commons.elasticsearch.core.query;

public class ElasticSearchHighlightEntry {

    private final String fieldName;
    private final int fragmentSize;
    private final int fragmentNumber;

    public ElasticSearchHighlightEntry(String fieldName, int fragmentSize, int fragmentNumber) {
        super();
        this.fieldName = fieldName;
        this.fragmentSize = fragmentSize;
        this.fragmentNumber = fragmentNumber;
    }

    public String getFieldName() {
        return fieldName;
    }

    public int getFragmentSize() {
        return fragmentSize;
    }

    public int getFragmentNumber() {
        return fragmentNumber;
    }

}

