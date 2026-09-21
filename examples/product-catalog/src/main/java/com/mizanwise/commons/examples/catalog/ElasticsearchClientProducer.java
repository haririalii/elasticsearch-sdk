package com.mizanwise.commons.examples.catalog;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.mizanwise.commons.elasticsearch.core.dao.RestClientFactory;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.util.List;

/** Creates one Elasticsearch client for the lifetime of the Quarkus application. */
public final class ElasticsearchClientProducer {
    @ConfigProperty(name = "catalog.elasticsearch.endpoint")
    String endpoint;

    @Produces
    ElasticsearchClient client() {
        return RestClientFactory.create(List.of(endpoint), null, null);
    }

    void close(@Disposes ElasticsearchClient client) throws IOException {
        RestClientFactory.close(client);
    }
}
