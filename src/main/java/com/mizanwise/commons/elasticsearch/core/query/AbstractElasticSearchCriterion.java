package com.mizanwise.commons.elasticsearch.core.query;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class AbstractElasticSearchCriterion implements ElasticSearchCriterion {

    protected boolean isFilter;
    protected float boostValue;

    public AbstractElasticSearchCriterion(Boolean isFilter, float boostValue) {
        super();
        this.isFilter = isFilter;
        this.boostValue = boostValue;
    }

    public abstract void addPrefixFieldName(String prefixFieldName);

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + this.toStringData();
    }

    protected Map<String, Object> toStringData() {
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("effect", !this.isFilter);
        data.put("boostValue", this.boostValue);

        return data;
    }

    @Override
    public Boolean isFilter() {
        return this.isFilter;
    }

    @Override
    public float getBoostValue() {
        return this.boostValue;
    }

}
