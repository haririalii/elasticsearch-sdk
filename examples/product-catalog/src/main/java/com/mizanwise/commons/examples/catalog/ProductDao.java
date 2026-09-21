package com.mizanwise.commons.examples.catalog;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.mizanwise.commons.elasticsearch.core.dao.AbstractElasticsearchDAO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public final class ProductDao extends AbstractElasticsearchDAO<ProductDocument> {
    @Inject
    public ProductDao(ElasticsearchClient client) {
        super(client);
    }

    @Override
    public Class<ProductDocument> getMappingClass() {
        return ProductDocument.class;
    }
}
