package com.mizanwise.commons.elasticsearch.core.query;

import com.mizanwise.commons.elasticsearch.core.mapping.MappingUtils;

import java.util.Map;

public abstract class SingleFieldSearchCriterion extends AbstractElasticSearchCriterion {

    protected String fieldName;

    public SingleFieldSearchCriterion(String fieldName, Boolean isFilter, float score) {
        super(isFilter, score);
        this.fieldName = fieldName;
    }

    protected static String appendKeyword(String fieldName) {
        return fieldName + "." + MappingUtils.TYPE_KEYWORD;
    }

    @Override
    public void addPrefixFieldName(String prefixFieldName) {
        if (fieldName != null)
            fieldName = prefixFieldName + "." + fieldName;
    }

    @Override
    protected Map<String, Object> toStringData() {
        Map<String, Object> data = super.toStringData();
        data.put("fieldName", this.fieldName);
        return data;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
}
