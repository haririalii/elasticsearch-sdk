package com.mizanwise.commons.examples.catalog;

import com.mizanwise.commons.elasticsearch.core.mapping.ElasticsearchMapping;
import com.mizanwise.commons.elasticsearch.core.mapping.annotation.Index;
import com.mizanwise.commons.elasticsearch.core.mapping.annotation.Keyword;
import com.mizanwise.commons.elasticsearch.core.mapping.annotation.Property;
import com.mizanwise.commons.elasticsearch.core.mapping.annotation.Text;

/** Elasticsearch representation of a {@link Product}. */
@Index("products")
public final class ProductDocument implements ElasticsearchMapping<Product> {
    @Property
    @Keyword
    private String id;

    @Property
    @Text(analyzer = "standard")
    private String name;

    @Property
    @Text(analyzer = "standard")
    private String description;

    @Property
    private long price;

    public ProductDocument() {
        // Required when the SDK deserializes search hits.
    }

    @Override
    public void loadFrom(Product product) {
        id = product.id();
        name = product.name();
        description = product.description();
        price = product.price();
    }

    @Override
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public long getPrice() {
        return price;
    }
}
