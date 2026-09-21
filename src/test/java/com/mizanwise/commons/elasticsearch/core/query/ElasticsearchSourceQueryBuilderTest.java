package com.mizanwise.commons.elasticsearch.core.query;

import com.mizanwise.commons.elasticsearch.core.query.model.SortDirection;
import com.mizanwise.commons.elasticsearch.core.query.model.SortEntry;
import com.mizanwise.commons.elasticsearch.core.query.restriction.ElasticLikeQueryRestriction;
import com.mizanwise.commons.elasticsearch.core.query.restriction.QueryComposition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ElasticsearchSourceQueryBuilderTest {
    @Test
    void buildsQueryAndSortFromLibraryOwnedTypes() {
        var query = new ElasticSearchQuery("products", QueryComposition.AND);
        query.addCriterion(new ElasticLikeQueryRestriction("name", "phone"));
        query.addSort(new SortEntry("name", SortDirection.ASCENDING));

        var source = ElasticsearchSourceQueryBuilder.buildSearchRequest(query);

        assertTrue(source.query().toString().contains("name.keyword"));
        assertEquals(1, source.sorts().size());
    }
}
