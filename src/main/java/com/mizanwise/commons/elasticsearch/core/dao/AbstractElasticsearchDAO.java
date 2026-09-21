package com.mizanwise.commons.elasticsearch.core.dao;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.JsonData;

import com.mizanwise.commons.elasticsearch.core.dao.utils.SearchRequestUtils;
import com.mizanwise.commons.elasticsearch.core.exception.ElasticsearchServiceException;
import com.mizanwise.commons.elasticsearch.core.index.ElasticIndexRequest;
import com.mizanwise.commons.elasticsearch.core.index.response.IndexResponse;
import com.mizanwise.commons.elasticsearch.core.mapping.ElasticMappingRequest;
import com.mizanwise.commons.elasticsearch.core.mapping.ElasticsearchMapping;
import com.mizanwise.commons.elasticsearch.core.search.ElasticSearchRequest;
import com.mizanwise.commons.elasticsearch.core.search.ReturnType;
import com.mizanwise.commons.elasticsearch.core.search.response.SearchItem;
import com.mizanwise.commons.elasticsearch.core.search.response.SearchResponse;
import com.mizanwise.commons.elasticsearch.core.utils.IndexUtils;
import com.mizanwise.commons.elasticsearch.core.utils.JsonUtils;
import jakarta.data.page.Page;
import jakarta.data.page.PageRequest;
import jakarta.data.page.impl.PageRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import java.util.List;

/**
 * Elasticsearch DAO backed by a shared {@link ElasticsearchClient}.
 */
public abstract class AbstractElasticsearchDAO<E extends ElasticsearchMapping<?>> implements ElasticsearchDAO<E> {

    private static final String SCROLL_TTL = "3m";
    private final ElasticsearchClient client;
    private String cachedIndexName;

    protected AbstractElasticsearchDAO(ElasticsearchClient client) {
        this.client = java.util.Objects.requireNonNull(client, "client");
    }

    public abstract Class<E> getMappingClass();

    @Override
    public Page<SearchItem<E>> search(ElasticSearchRequest searchRequest, PageRequest pageRequest)
            throws ElasticsearchServiceException {
        java.util.Objects.requireNonNull(pageRequest, "pageRequest");
        if (pageRequest.mode() != PageRequest.Mode.OFFSET) {
            throw new UnsupportedOperationException("Elasticsearch cursor pagination is not supported");
        }

        int offset = offsetFor(pageRequest);
        int originalFrom = searchRequest.source().from();
        int originalSize = searchRequest.source().size();
        SearchResponse<E> response;
        try {
            response = search(searchRequest.from(offset).size(pageRequest.size()));
        } finally {
            searchRequest.from(originalFrom).size(originalSize);
        }
        long totalElements = pageRequest.requestTotal() ? count(searchRequest) : -1;
        boolean moreResults = pageRequest.requestTotal()
                ? offset + (long) response.getItems().size() < totalElements
                : response.getItems().size() == pageRequest.size();

        return new PageRecord<>(pageRequest, response.getItems(), totalElements, moreResults);
    }

    private static int offsetFor(PageRequest pageRequest) {
        try {
            return Math.toIntExact(Math.multiplyExact(pageRequest.page() - 1, pageRequest.size()));
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException("Page offset exceeds Elasticsearch's integer offset limit", e);
        }
    }

    @Override
    public SearchResponse<E> search(ElasticSearchRequest searchRequest) throws ElasticsearchServiceException {
        try {
            return searchRequest.scrollId() != null ? scrollSearch(searchRequest) : normalSearch(searchRequest);
        } catch (IOException e) {
            throw new ElasticsearchServiceException(e);
        }
    }

    @Override
    public IndexResponse addOrUpdateIndexDocument(ElasticIndexRequest<E> indexRequest)
            throws ElasticsearchServiceException {
        try {
            return new IndexResponse(
                    client.index(request -> request.index(getIndexName())
                            .id(indexRequest.id())
                            .document(JsonData.fromJson(JsonUtils.convertToString(indexRequest.data())))
                            .refresh(indexRequest.refreshPolicy()))
            );
        } catch (IOException e) {
            throw new ElasticsearchServiceException(e);
        }
    }

    @Override
    public IndexResponse deleteIndexDocument(ElasticIndexRequest<E> indexRequest) throws ElasticsearchServiceException {
        try {
            return new IndexResponse(
                    client.delete(request -> request.index(getIndexName())
                            .id(indexRequest.id())
                            .refresh(indexRequest.refreshPolicy()))
            );
        } catch (IOException e) {
            throw new ElasticsearchServiceException(e);
        }
    }

    @Override
    public boolean createOrUpdateMapping(ElasticMappingRequest<E> mappingRequest) throws ElasticsearchServiceException {
        boolean indexExists = indexExists(new ElasticIndexRequest<>(this.getIndexName()));
        try {
            if (indexExists) {
                return client.indices()
                        .putMapping(request -> request.index(getIndexName())
                                .withJson(new StringReader(mappingRequest.xContentBuilder().toString())))
                        .acknowledged();
            } else {
                String index = this.getIndexName();
                String indexWithPrefix = index + "_" + new Date().getTime();
                String body = "{\"mappings\":" + mappingRequest.xContentBuilder() +
                        (StringUtils.isNotBlank(mappingRequest.defaultAnalyzer())
                                ? ",\"settings\":{\"analysis\":{\"analyzer\":{\"default\":{\"type\":\"" +
                                mappingRequest.defaultAnalyzer() + "\"}}}}"
                                : "") + "}";
                boolean create = client.indices()
                        .create(request -> request.index(indexWithPrefix).withJson(new StringReader(body)))
                        .acknowledged();

                boolean alias = client.indices()
                        .updateAliases(request -> request.actions(action -> action.add(add -> add
                                .index(indexWithPrefix)
                                .alias(index))))
                        .acknowledged();

                return create && alias;
            }
        } catch (IOException e) {
            throw new ElasticsearchServiceException(e);
        }
    }

    @Override
    public boolean indexExists(ElasticIndexRequest<E> indexRequest) throws ElasticsearchServiceException {
        try {
            return client.indices().exists(request -> request.index(getIndexName())).value();
        } catch (IOException e) {
            throw new ElasticsearchServiceException(e);
        }
    }

    @Override
    public boolean deleteIndex(ElasticIndexRequest<E> request) throws ElasticsearchServiceException {
        try {
            return client.indices().delete(deleteRequest -> deleteRequest.index(getIndexName()))
                    .acknowledged();
        } catch (IOException e) {
            throw new ElasticsearchServiceException(e);
        }
    }

    /**
     * Count request
     *
     * @param searchRequest count request name
     * @return count of the query result
     * @throws ElasticsearchServiceException elasticsearch connection error
     */
    @Override
    public Long count(ElasticSearchRequest searchRequest) throws ElasticsearchServiceException {
        try {
            return client.count(request -> request
                            .index(List.of(getIndicesNameForSearch(searchRequest)))
                            .withJson(new StringReader("{\"query\":" + searchRequest.source().query() + "}")))
                    .count();
        } catch (IOException e) {
            throw new ElasticsearchServiceException(e);
        }
    }

    /**
     * do search on elasticsearch
     *
     * @param searchRequest payload of the search
     * @return result of the search
     * @throws IOException elasticsearch connection error
     */
    public SearchResponse<E> normalSearch(ElasticSearchRequest searchRequest) throws IOException {
        if (ReturnType.ID == searchRequest.returnType())
            searchRequest.source().fetchSource(false);
        co.elastic.clients.elasticsearch.core.SearchResponse<JsonData> searchResponse = client.search(request -> {
            request.index(List.of(getIndicesNameForSearch(searchRequest)))
                    .withJson(new StringReader(searchRequest.source().toString()));
            if (searchRequest.scrollAble()) request.scroll(scroll -> scroll.time(SCROLL_TTL));
            return request;
        }, JsonData.class);
        clearScroll(searchResponse, searchRequest.scrollId());

        return SearchRequestUtils.getResponse(searchResponse, this.getMappingClass());
    }

    /**
     * start search at elasticsearch with scroll api
     *
     * @param searchRequest payload of the search
     * @return scroll able search result
     * @throws IOException elasticsearch connection error
     */
    public SearchResponse<E> scrollSearch(ElasticSearchRequest searchRequest) throws IOException {
        String scrollId = searchRequest.scrollId();
        return SearchRequestUtils.getResponse(
                client.scroll(request -> request.scrollId(scrollId).scroll(scroll -> scroll.time(SCROLL_TTL)), JsonData.class),
                this.getMappingClass()
        );
    }

    /**
     * clear scroll when search doesn't have more hit for scroll searches
     *
     * @param searchResponse response of the search
     * @param scrollId       scroll id
     * @throws IOException elasticsearch connection error
     */
    private void clearScroll(co.elastic.clients.elasticsearch.core.SearchResponse<JsonData> searchResponse, String scrollId)
            throws IOException {
        // check for search response doesn't have any result
        if (scrollId != null && searchResponse.hits().hits().isEmpty()) {
            client.clearScroll(request -> request.scrollId(scrollId));
        }
    }

    private String[] getIndicesNameForSearch(ElasticSearchRequest searchRequest) {
        if (CollectionUtils.isEmpty(searchRequest.indices()))
            return new String[]{this.getIndexName()};

        return searchRequest.indices()
                .stream()
                .map(IndexUtils::appendIndexPrefix)
                .toArray(String[]::new);
    }

    private String getIndexName() {
        if (cachedIndexName == null)
            cachedIndexName = IndexUtils.getIndexNameWithPrefix(this.getMappingClass());

        return cachedIndexName;
    }

}
