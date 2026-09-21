package com.mizanwise.commons.elasticsearch.core.dao;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RestClientFactoryTest {

    @Test
    void rejectsEmptyEndpointList() {
        assertThrows(IllegalArgumentException.class, () -> RestClientFactory.create(List.of(), null, null));
    }

    @Test
    void rejectsUsernameWithoutPassword() {
        assertThrows(IllegalArgumentException.class,
                () -> RestClientFactory.create(List.of("http://localhost:9200"), "elastic", null));
    }

    @Test
    void createsClientForValidEndpoint() {
        ElasticsearchClient client = RestClientFactory.create(List.of("http://localhost:9200"), null, null);
        try {
            assertDoesNotThrow(() -> RestClientFactory.close(client));
        } catch (Exception exception) {
            throw new AssertionError(exception);
        }
    }
}
