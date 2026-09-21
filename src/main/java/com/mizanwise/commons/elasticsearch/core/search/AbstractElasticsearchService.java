package com.mizanwise.commons.elasticsearch.core.search;

import com.mizanwise.commons.elasticsearch.core.dao.ElasticsearchDAO;
import com.mizanwise.commons.elasticsearch.core.exception.ElasticsearchServiceException;
import com.mizanwise.commons.elasticsearch.core.exception.InsufficientFiledException;
import com.mizanwise.commons.elasticsearch.core.index.ElasticIndexRequest;
import com.mizanwise.commons.elasticsearch.core.index.response.IndexResponse;
import com.mizanwise.commons.elasticsearch.core.mapping.ElasticMappingRequest;
import com.mizanwise.commons.elasticsearch.core.mapping.ElasticsearchMapping;
import com.mizanwise.commons.elasticsearch.core.mapping.MappingException;
import com.mizanwise.commons.elasticsearch.core.mapping.MappingUtils;
import com.mizanwise.commons.elasticsearch.core.search.response.SearchItem;
import com.mizanwise.commons.elasticsearch.core.search.response.SearchResponse;
import com.mizanwise.commons.elasticsearch.core.utils.ConfigProvider;
import jakarta.data.page.Page;
import jakarta.data.page.PageRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base service that validates requests and delegates persistence work to an Elasticsearch DAO.
 *
 * <p>Subclasses normally expose this service as a CDI bean and provide an application-scoped DAO.</p>
 */
public abstract class AbstractElasticsearchService<E extends ElasticsearchMapping<?>> implements ElasticsearchService<E> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractElasticsearchService.class);

    protected abstract ElasticsearchDAO<E> getDAO();

    @Override
    public Page<SearchItem<E>> search(ElasticSearchRequest searchRequest, PageRequest pageRequest)
            throws ElasticsearchServiceException {
        validate();
        if (!searchRequest.validateForSearch())
            throw new InsufficientFiledException();

        return getDAO().search(searchRequest, pageRequest);
    }

    @Override
    public SearchResponse<E> search(ElasticSearchRequest searchRequest) throws ElasticsearchServiceException {
        validate();
        if (!searchRequest.validateForSearch())
            throw new InsufficientFiledException();

        return getDAO().search(searchRequest);
    }

    @Override
    public Long count(ElasticSearchRequest searchRequest) throws ElasticsearchServiceException {
        validate();
        if (!searchRequest.validateForCount())
            throw new InsufficientFiledException();

        return getDAO().count(searchRequest);
    }


    @Override
    public IndexResponse addOrUpdate(ElasticIndexRequest<E> indexRequest) throws ElasticsearchServiceException {
        validate();
        if (!indexRequest.validateForAddOrUpdate())
            throw new InsufficientFiledException();

        IndexResponse response = getDAO().addOrUpdateIndexDocument(indexRequest);
        logger.debug("Add/update response: {}", response);
        return response;
    }

    @Override
    public IndexResponse delete(ElasticIndexRequest<E> indexRequest) throws ElasticsearchServiceException {
        validate();
        if (!indexRequest.validateForDelete())
            throw new InsufficientFiledException();

        IndexResponse response = getDAO().deleteIndexDocument(indexRequest);
        logger.debug("Delete response: {}", response);
        return response;
    }

    @Override
    public boolean createOrUpdateMapping(ElasticMappingRequest<E> mappingRequest)
            throws ElasticsearchServiceException, MappingException {
        mappingRequest.initialXContentBuilder();
        MappingUtils.createMapping(mappingRequest.mappingClass(), mappingRequest.defaultAnalyzer(), mappingRequest.xContentBuilder());

        return getDAO().createOrUpdateMapping(mappingRequest);
    }

    private void validate() {
        ConfigProvider.checkDisabled();
    }

}
