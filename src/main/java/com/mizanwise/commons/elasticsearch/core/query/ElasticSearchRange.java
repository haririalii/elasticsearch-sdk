package com.mizanwise.commons.elasticsearch.core.query;

public class ElasticSearchRange {

    private Long to;
    private Long from;

    public ElasticSearchRange(Long from, Long to) {
        this.from = from;
        this.to = to;
    }

    public boolean hasTo() {
        return this.to != null;
    }

    public boolean hasFrom() {
        return this.from != null;
    }

    public boolean hasAll() {
        return this.hasFrom() && this.hasTo();
    }

    public Long getTo() {
        return to;
    }

    public void setTo(Long to) {
        this.to = to;
    }

    public Long getFrom() {
        return from;
    }

    public void setFrom(Long from) {
        this.from = from;
    }
}
