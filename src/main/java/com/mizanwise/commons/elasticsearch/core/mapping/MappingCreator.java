package com.mizanwise.commons.elasticsearch.core.mapping;

import com.mizanwise.commons.elasticsearch.core.search.ElasticsearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

/**
 * Creates mappings explicitly during an application's startup lifecycle.
 */
public final class MappingCreator {
    private static final Logger LOG = LoggerFactory.getLogger(MappingCreator.class);
    private final ElasticsearchService<ElasticsearchMapping<?>> service;
    private List<Class<? extends ElasticsearchMapping<?>>> mappings = List.of();
    private String defaultAnalyzer;

    public MappingCreator(ElasticsearchService<ElasticsearchMapping<?>> service) {
        this.service = Objects.requireNonNull(service, "service");
    }

    public void createMappings() {
        for (Class<? extends ElasticsearchMapping<?>> mapping : mappings) {
            try {
                @SuppressWarnings({"rawtypes", "unchecked"})
                var request = new ElasticMappingRequest((Class) mapping, defaultAnalyzer);
                service.createOrUpdateMapping(request);
                LOG.info("Mapping created or updated: {}", mapping.getName());
            } catch (Exception exception) {
                throw new IllegalStateException("Unable to create mapping " + mapping.getName(), exception);
            }
        }
    }

    public void setMappings(List<Class<? extends ElasticsearchMapping<?>>> mappings) {
        this.mappings = List.copyOf(mappings);
    }

    public String getDefaultAnalyzer() {
        return defaultAnalyzer;
    }

    public void setDefaultAnalyzer(String defaultAnalyzer) {
        this.defaultAnalyzer = defaultAnalyzer;
    }
}
