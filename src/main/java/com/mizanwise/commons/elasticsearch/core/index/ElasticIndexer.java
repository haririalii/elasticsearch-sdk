package com.mizanwise.commons.elasticsearch.core.index;

import com.mizanwise.commons.elasticsearch.core.index.listener.ActionListener;
import com.mizanwise.commons.elasticsearch.core.index.response.IndexResponse;
import com.mizanwise.commons.elasticsearch.core.mapping.ElasticsearchMapping;
import com.mizanwise.commons.elasticsearch.core.mapping.MappingException;
import com.mizanwise.commons.elasticsearch.core.search.ElasticsearchService;
import com.mizanwise.commons.elasticsearch.core.utils.ConfigProvider;
import co.elastic.clients.elasticsearch._types.Refresh;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Coordinates indexing work for a mapping type.
 *
 * <p>For Jakarta EE applications, provide a container-managed executor (for example, a
 * {@code ManagedExecutorService}).</p>
 *
 * @param <E> the entity witch you want index in elasticsearch
 */
public abstract class ElasticIndexer<E, M extends ElasticsearchMapping<E>> {

    private static final Logger logger = LoggerFactory.getLogger(ElasticIndexer.class);
    private final Executor executor;

    /**
     * Creates an indexer using an application-provided executor.
     *
     * @param executor executor used by asynchronous operations
     */
    protected ElasticIndexer(Executor executor) {
        this.executor = java.util.Objects.requireNonNull(executor, "executor");
    }

    protected abstract ElasticsearchService<M> getElasticsearchService();

    protected abstract Class<M> getMappingClass();

    public void start(E e, IndexOperation operation) {
        this.start(e, operation, null);
    }

    /**
     * Executes an index request at elasticsearch
     *
     * @param e         entity to index
     * @param operation operation of the index request
     */
    public void start(E e, IndexOperation operation, ActionListener<E> listener) {
        this.start(
                Collections.singletonList(e),
                operation,
                listener,
                null
        );
    }

    public void startAsync(E e, IndexOperation operation) {
        this.startAsync(e, operation, null);
    }

    /**
     * Asynchronously executes an index request at elasticsearch
     *
     * @param e         entity to index
     * @param operation operation of the index request
     * @param listener  the listener to be notified upon request completion
     */
    public void startAsync(E e, IndexOperation operation, ActionListener<E> listener) {
        this.startAsync(
                Collections.singletonList(e),
                operation,
                listener,
                null
        );
    }

    public void start(List<E> entities, IndexOperation operation) {
        this.start(entities, operation, null, null);
    }

    /**
     * Executes an index request for the collection of objects
     *
     * @param entities  entities
     * @param operation operation of the index request
     */
    public void start(List<E> entities, IndexOperation operation, ActionListener<E> listener, Refresh refreshPolicy) {
        ConfigProvider.checkDisabled();

        new IndexerTask(
                entities,
                operation,
                listener,
                refreshPolicy
        ).run();
    }

    public void startAsync(List<E> entities, IndexOperation operation) {
        this.startAsync(entities, operation, null, null);
    }

    /**
     * Executes an index request for the collection of objects asynchronously
     *
     * @param entities  entities
     * @param operation operation of the index request
     */
    public void startAsync(List<E> entities, IndexOperation operation, ActionListener<E> listener, Refresh refreshPolicy) {
        ConfigProvider.checkDisabled();

        executor.execute(
                new IndexerTask(
                        entities,
                        operation,
                        listener,
                        refreshPolicy
                )
        );
    }

    /**
     * Create elasticsearch add or update index request
     *
     * @param entity the entity
     * @return index request
     */
    private ElasticIndexRequest<M> addOrUpdateRequest(E entity, Refresh refreshPolicy) throws MappingException {
        M mapping = createNew();
        mapping.loadFrom(entity);

        return new ElasticIndexRequest<>(mapping.getId(), mapping, refreshPolicy);
    }

    /**
     * Create elasticsearch delete index request
     *
     * @param entity the entity
     * @return index request
     */
    private ElasticIndexRequest<M> deleteRequest(E entity, Refresh refreshPolicy) throws MappingException {
        ElasticsearchMapping<E> mapping = createNew();
        mapping.loadFrom(entity);

        return new ElasticIndexRequest<>(mapping.getId(), null, refreshPolicy);
    }

    private M createNew() {
        try {
            return this.getMappingClass().getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException("Could not instantiate mapping type " + this.getMappingClass(), e);
        }
    }

    private static class DefaultActionListener<E> implements ActionListener<E> {

        @Override
        public void onSuccess(E e, IndexResponse response) {
            logger.debug("Entity indexed successfully with result {}: {}", response.getElasticResult(), e);
        }

        @Override
        public void onFailure(E e, Exception exception) {
            logger.error("Entity index error for {}", e, exception);
        }
    }

    private class IndexerTask implements Runnable {

        private List<E> entities;
        private IndexOperation operation;
        private ActionListener<E> listener;
        private Refresh refreshPolicy;

        public IndexerTask(List<E> entities, IndexOperation operation, ActionListener<E> listener, Refresh refreshPolicy) {
            this.entities = entities;
            this.operation = operation;
            this.listener = listener;
            this.refreshPolicy = refreshPolicy;
        }

        @Override
        public void run() {
            if (this.entities == null) {
                logger.warn("Entities cannot be null!");
                return;
            }

            if (this.listener == null)
                this.listener = new DefaultActionListener<>();

            ElasticsearchService<M> elasticsearchService = getElasticsearchService();

            for (E entity : this.entities) {
                try {
                    IndexResponse response;
                    switch (operation) {
                        case ADD: {
                            response = elasticsearchService.addOrUpdate(
                                    addOrUpdateRequest(entity, refreshPolicy)
                            );
                            break;
                        }
                        case DELETE: {
                            response = elasticsearchService.delete(
                                    deleteRequest(entity, refreshPolicy)
                            );
                            break;
                        }
                        default:
                            throw new IllegalArgumentException("Unsupported index operation");
                    }
                    this.listener.onSuccess(entity, response);
                } catch (Exception e) {
                    this.listener.onFailure(entity, e);
                }
            }
        }
    }

}
