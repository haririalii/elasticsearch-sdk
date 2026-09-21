package com.mizanwise.commons.examples.catalog;

import com.mizanwise.commons.elasticsearch.core.index.ElasticIndexRequest;
import com.mizanwise.commons.elasticsearch.core.mapping.ElasticMappingRequest;
import com.mizanwise.commons.elasticsearch.core.search.ElasticSearchRequest;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.List;

/** HTTP use case for indexing and searching a small product catalog. */
@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
public final class ProductResource {
    @Inject
    ProductSearchService service;

    @POST
    @Path("/seed")
    public List<ProductDocument> seed() throws Exception {
        service.createOrUpdateMapping(new ElasticMappingRequest<>(ProductDocument.class, "standard"));
        for (Product product : sampleProducts()) {
            ProductDocument document = new ProductDocument();
            document.loadFrom(product);
            service.addOrUpdate(new ElasticIndexRequest<>(document.getId(), document));
        }
        return search("*");
    }

    @GET
    public List<ProductDocument> search(@QueryParam("q") String query) throws Exception {
        String searchTerm = query == null || query.isBlank() ? "*" : query;
        ElasticSearchRequest request = new ElasticSearchRequest("products")
                .queries(QueryBuilders.queryStringQuery(searchTerm).field("name"))
                .from(0)
                .size(20);
        return service.search(request).getEntities();
    }

    private static List<Product> sampleProducts() {
        return List.of(
                new Product("1", "Wireless keyboard", "Compact wireless keyboard with a quiet key switch", 75),
                new Product("2", "Wireless mouse", "Ergonomic mouse for daily work", 45),
                new Product("3", "USB-C dock", "Docking station with HDMI and Ethernet", 130)
        );
    }
}
