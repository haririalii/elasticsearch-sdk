package com.mizanwise.commons.elasticsearch.core.search;

import com.mizanwise.commons.elasticsearch.core.exception.ElasticsearchServiceException;
import com.mizanwise.commons.elasticsearch.core.index.ElasticIndexRequest;
import com.mizanwise.commons.elasticsearch.core.index.response.IndexResponse;
import com.mizanwise.commons.elasticsearch.core.mapping.ElasticMappingRequest;
import com.mizanwise.commons.elasticsearch.core.mapping.ElasticsearchMapping;
import com.mizanwise.commons.elasticsearch.core.mapping.MappingException;
import com.mizanwise.commons.elasticsearch.core.search.response.SearchItem;
import com.mizanwise.commons.elasticsearch.core.search.response.SearchResponse;
import jakarta.data.page.Page;
import jakarta.data.page.PageRequest;

/**
 * Service-level contract for searching, indexing, and mapping an Elasticsearch document type.
 *
 * @param <E> Elasticsearch mapping type served by this instance
 */
public interface ElasticsearchService<E extends ElasticsearchMapping<?>> {

    /**
     * Executes one offset-based page of a search result.
     */
    Page<SearchItem<E>> search(ElasticSearchRequest searchRequest, PageRequest pageRequest)
            throws ElasticsearchServiceException;

    /**
     * Executes a search request.
     */
    SearchResponse<E> search(ElasticSearchRequest searchRequest) throws ElasticsearchServiceException;

    /**
     * Counts documents that match a search request.
     */
    Long count(ElasticSearchRequest searchRequest) throws ElasticsearchServiceException;

    /**
     * Creates or replaces an indexed document.
     */
    IndexResponse addOrUpdate(ElasticIndexRequest<E> indexRequest) throws ElasticsearchServiceException;

    /**
     * Deletes an indexed document.
     */
    IndexResponse delete(ElasticIndexRequest<E> indexRequest) throws ElasticsearchServiceException;

    /**
     * Creates the index or updates its mapping.
     */
    boolean createOrUpdateMapping(ElasticMappingRequest<E> mappingRequest)
            throws ElasticsearchServiceException, MappingException;
}
