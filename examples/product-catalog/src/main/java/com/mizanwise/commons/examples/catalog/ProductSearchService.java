package com.mizanwise.commons.examples.catalog;

import com.mizanwise.commons.elasticsearch.core.dao.ElasticsearchDAO;
import com.mizanwise.commons.elasticsearch.core.search.AbstractElasticsearchService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public final class ProductSearchService extends AbstractElasticsearchService<ProductDocument> {
    private final ProductDao dao;

    @Inject
    public ProductSearchService(ProductDao dao) {
        this.dao = dao;
    }

    @Override
    protected ElasticsearchDAO<ProductDocument> getDAO() {
        return dao;
    }
}
