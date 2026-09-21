package com.mizanwise.commons.elasticsearch.core.mapping;

import org.elasticsearch.xcontent.XContentBuilder;
import org.elasticsearch.xcontent.XContentFactory;

import java.io.IOException;

/**
 * Holds the mapping type, optional analyzer, and generated mapping content for an index request.
 */
public class ElasticMappingRequest<E extends ElasticsearchMapping<?>> {

    private final Class<E> mappingClass;
    private XContentBuilder xContentBuilder;
    private String defaultAnalyzer;

    public ElasticMappingRequest(Class<E> mappingClass, String defaultAnalyzer) {
        this.mappingClass = mappingClass;
        this.defaultAnalyzer = defaultAnalyzer;
    }

    public ElasticMappingRequest(Class<E> mappingClass) {
        this.mappingClass = mappingClass;
    }

    /**
     * Initializes the JSON builder used to generate the mapping.
     */
    public void initialXContentBuilder() {
        try {
            this.xContentBuilder = XContentFactory.jsonBuilder();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Class<?> mappingClass() {
        return mappingClass;
    }

    public XContentBuilder xContentBuilder() {
        return xContentBuilder;
    }

    public String defaultAnalyzer() {
        return defaultAnalyzer;
    }
}
