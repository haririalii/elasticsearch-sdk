package com.mizanwise.commons.elasticsearch.core.dao;

import com.mizanwise.commons.elasticsearch.core.exception.ElasticsearchServiceException;
import com.mizanwise.commons.elasticsearch.core.index.ElasticIndexRequest;
import com.mizanwise.commons.elasticsearch.core.index.response.IndexResponse;
import com.mizanwise.commons.elasticsearch.core.mapping.ElasticMappingRequest;
import com.mizanwise.commons.elasticsearch.core.mapping.ElasticsearchMapping;
import com.mizanwise.commons.elasticsearch.core.search.ElasticSearchRequest;
import com.mizanwise.commons.elasticsearch.core.search.response.SearchItem;
import com.mizanwise.commons.elasticsearch.core.search.response.SearchResponse;
import jakarta.data.page.Page;
import jakarta.data.page.PageRequest;

/**
 * This interface defines the contract for interacting with Elasticsearch, including search, index management, and mapping operations.
 */
public interface ElasticsearchDAO<E extends ElasticsearchMapping<?>> {

    /**
     * Executes one offset-based page of a search result.
     *
     * <p>Cursor-based {@link PageRequest}s are not supported because this API currently
     * exposes Elasticsearch offset pagination only.</p>
     */
    Page<SearchItem<E>> search(ElasticSearchRequest searchRequest, PageRequest pageRequest)
            throws ElasticsearchServiceException;

    /**
     * Perform a search operation in Elasticsearch.
     *
     * @param searchRequest The payload of the search request.
     * @return The result of the search operation.
     * @throws ElasticsearchServiceException If there is an error in communicating with Elasticsearch.
     */
    SearchResponse<E> search(ElasticSearchRequest searchRequest) throws ElasticsearchServiceException;

    /**
     * Perform a count request in Elasticsearch.
     *
     * @param searchRequest The payload of the search request.
     * @return The total item count.
     * @throws ElasticsearchServiceException If there is an error in communicating with Elasticsearch.
     */
    Long count(ElasticSearchRequest searchRequest) throws ElasticsearchServiceException;

    /**
     * Create or update a document in a specific index.
     *
     * @param indexRequest The payload of the index request.
     * @return The result of the index request.
     * @throws ElasticsearchServiceException If there is an error in communicating with Elasticsearch.
     */
    IndexResponse addOrUpdateIndexDocument(ElasticIndexRequest<E> indexRequest) throws ElasticsearchServiceException;

    /**
     * Delete a document from a specific index.
     *
     * @param indexRequest The payload of the index request.
     * @return The result of the index request.
     * @throws ElasticsearchServiceException If there is an error in communicating with Elasticsearch.
     */
    IndexResponse deleteIndexDocument(ElasticIndexRequest<E> indexRequest) throws ElasticsearchServiceException;

    /**
     * Create or update an Elasticsearch mapping.
     *
     * @param mappingRequest The mapping request.
     * @return Acknowledgment of the mapping creation or update.
     * @throws ElasticsearchServiceException If there is an error in communicating with Elasticsearch.
     */
    boolean createOrUpdateMapping(ElasticMappingRequest<E> mappingRequest) throws ElasticsearchServiceException;

    /**
     * Check the existence of an index.
     *
     * @param request The index request.
     * @return `true` if the index exists; otherwise, `false`.
     * @throws ElasticsearchServiceException If there is an error in communicating with Elasticsearch.
     */
    boolean indexExists(ElasticIndexRequest<E> request) throws ElasticsearchServiceException;

    /**
     * Delete an Elasticsearch index.
     *
     * @param indexRequest The index request.
     * @return Acknowledgment of the index deletion.
     * @throws ElasticsearchServiceException If there is an error in communicating with Elasticsearch.
     */
    boolean deleteIndex(ElasticIndexRequest<E> indexRequest) throws ElasticsearchServiceException;

}
