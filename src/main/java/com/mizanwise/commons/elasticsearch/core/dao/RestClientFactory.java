package com.mizanwise.commons.elasticsearch.core.dao;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;

import java.util.Collection;
import java.util.Objects;

/**
 * Creates Elasticsearch clients from explicit application configuration.
 *
 * <p>The caller owns the returned client's transport and must close it during the application's shutdown
 * lifecycle. In CDI applications, create one application-scoped client rather than one per
 * request.</p>
 */
public final class RestClientFactory {
    private RestClientFactory() {
    }

    /**
     * Creates a client for one or more Elasticsearch HTTP endpoints.
     *
     * @param endpoints endpoint URLs accepted by {@link HttpHost#create(String)}
     * @param username  optional basic-auth username
     * @param password  password for {@code username}; required when a username is supplied
     * @return a new, open client
     * @throws IllegalArgumentException if no endpoint is provided or authentication is incomplete
     */
    public static ElasticsearchClient create(Collection<String> endpoints, String username, String password) {
        Objects.requireNonNull(endpoints, "endpoints");
        if (endpoints.isEmpty()) throw new IllegalArgumentException("At least one endpoint is required");
        RestClientBuilder builder = RestClient.builder(endpoints.stream().map(HttpHost::create).toArray(HttpHost[]::new));
        if (username != null && !username.isBlank()) {
            if (password == null) {
                throw new IllegalArgumentException("A password is required when a username is supplied");
            }
            CredentialsProvider credentials = new BasicCredentialsProvider();
            credentials.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
            builder.setHttpClientConfigCallback(http -> http.setDefaultCredentialsProvider(credentials));
        }
        return new ElasticsearchClient(new RestClientTransport(builder.build(), new JacksonJsonpMapper()));
    }

    /**
     * Closes the transport owned by a client created by this factory.
     */
    public static void close(ElasticsearchClient client) throws java.io.IOException {
        client._transport().close();
    }
}
