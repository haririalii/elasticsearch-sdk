# Elasticsearch SDK

Framework-neutral Java support for Elasticsearch document mappings, indexing, and search. The SDK is designed to fit Jakarta EE or Quarkus applications, but its public API does not require a framework.

It builds on the official Elasticsearch Java API client and provides:

- annotation-driven mapping generation;
- a reusable DAO and service layer for indexing, deleting, counting, and searching documents;
- query composition with restrictions, sorting, highlighting, aggregations, and scroll searches; and
- synchronous and executor-backed asynchronous indexing through `ElasticIndexer`.

## Compatibility

| Component | Version |
| --- | --- |
| Java | 25 or later |
| Elasticsearch Java API client | 8.17.0 |
| Maven | 3.9 or later |

The SDK uses the Elasticsearch 8.x Java API client for HTTP operations. Its public query-building API depends on Elasticsearch query-builder types, so consumers should keep their Elasticsearch dependencies aligned with the version declared by the SDK.

## Installation

The artifact is not currently published to a remote Maven repository. Build and install it locally:

```bash
mvn install
```

Then add it to a consuming Maven project:

```xml
<dependency>
    <groupId>com.mizanwise.commons</groupId>
    <artifactId>elasticsearch-sdk</artifactId>
    <version>2.0.0-SNAPSHOT</version>
</dependency>
```

## Quick start

### 1. Define a document mapping

Mapping classes implement `ElasticsearchMapping`, are annotated with `@Index`, and opt fields into the generated mapping with `@Property`. The mapping utilities inspect fields, including inherited fields.

```java
import com.mizanwise.commons.elasticsearch.core.mapping.ElasticsearchMapping;
import com.mizanwise.commons.elasticsearch.core.mapping.annotation.Index;
import com.mizanwise.commons.elasticsearch.core.mapping.annotation.Property;
import com.mizanwise.commons.elasticsearch.core.mapping.annotation.Text;

@Index("products")
public final class ProductDocument implements ElasticsearchMapping<Product> {
    @Property
    private String id;

    @Property
    @Text(analyzer = "standard")
    private String name;

    @Property
    private long price;

    @Override
    public void loadFrom(Product product) {
        id = product.id();
        name = product.name();
        price = product.price();
    }

    @Override
    public String getId() {
        return id;
    }
}
```

The supported field annotations are `@Text`, `@Keyword`, `@Date`, `@Nested`, and `@Version`. `@Property` can rename a field; `@MappingIgnore` prevents a class from being used as a top-level mapping.

### 2. Create one shared client

Create the client at application startup and close it when the application stops. `RestClientFactory` supports one or more endpoints and optional basic authentication.

```java
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.mizanwise.commons.elasticsearch.core.dao.RestClientFactory;
import java.util.List;

ElasticsearchClient client = RestClientFactory.create(
        List.of("http://localhost:9200"),
        null, // username, if required
        null  // password, if required
);

// At application shutdown:
RestClientFactory.close(client);
```

In a CDI application, expose this client as an application-scoped bean and inject it into DAOs. Do not create a client for each request.

### 3. Implement the DAO and service

```java
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.mizanwise.commons.elasticsearch.core.dao.AbstractElasticsearchDAO;
import com.mizanwise.commons.elasticsearch.core.dao.ElasticsearchDAO;
import com.mizanwise.commons.elasticsearch.core.search.AbstractElasticsearchService;

public final class ProductDao extends AbstractElasticsearchDAO<ProductDocument> {
    public ProductDao(ElasticsearchClient client) {
        super(client);
    }

    @Override
    public Class<ProductDocument> getMappingClass() {
        return ProductDocument.class;
    }
}

public final class ProductSearchService extends AbstractElasticsearchService<ProductDocument> {
    private final ProductDao dao;

    public ProductSearchService(ProductDao dao) {
        this.dao = dao;
    }

    @Override
    protected ElasticsearchDAO<ProductDocument> getDAO() {
        return dao;
    }
}
```

### 4. Create the mapping and work with documents

Set `-Delasticsearch.enable=true` before invoking service or indexer operations.

```java
import com.mizanwise.commons.elasticsearch.core.index.ElasticIndexRequest;
import com.mizanwise.commons.elasticsearch.core.mapping.ElasticMappingRequest;
import com.mizanwise.commons.elasticsearch.core.search.ElasticSearchRequest;
import org.elasticsearch.index.query.QueryBuilders;

var service = new ProductSearchService(new ProductDao(client));

service.createOrUpdateMapping(
        new ElasticMappingRequest<>(ProductDocument.class, "standard"));

var document = new ProductDocument();
document.loadFrom(product);
service.addOrUpdate(new ElasticIndexRequest<>(document.getId(), document));

var request = new ElasticSearchRequest("products")
        .queries(QueryBuilders.matchQuery("name", "wireless keyboard"))
        .from(0)
        .size(20);
var results = service.search(request);
```

When a mapped index does not yet exist, mapping creation creates a timestamped backing index and attaches the annotated index name as its alias. For an existing index or alias, it submits a mapping update.

## Configuration

`AbstractElasticsearchService` and `ElasticIndexer` are disabled unless the following JVM property is set:

```text
-Delasticsearch.enable=true
```

The SDK also recognizes these system properties. Endpoint and credential properties are helpers for application configuration; pass their values to `RestClientFactory.create` when creating the client.

| Property | Purpose | Default |
| --- | --- | --- |
| `elasticsearch.enable` | Enables service and indexer operations | `false` |
| `elasticsearch.endpoints` | Comma-separated Elasticsearch endpoint URLs | empty |
| `elasticsearch.username` | Basic-auth username | unset |
| `elasticsearch.password` | Basic-auth password | unset |
| `elasticsearch.index_prefix` | Prefix prepended to index names, followed by `_` | empty |
| `elasticsearch.mapping.creator.enabled` | Reserved mapping-creator setting | unset |

For example, an application may create its client from configured system properties:

```java
import com.mizanwise.commons.elasticsearch.core.dao.RestClientFactory;
import com.mizanwise.commons.elasticsearch.core.utils.ConfigProvider;

var client = RestClientFactory.create(
        ConfigProvider.getEndpoints(),
        System.getProperty(ConfigProvider.PROP_CONFIG_USERNAME),
        System.getProperty(ConfigProvider.PROP_CONFIG_PASSWORD));
```

## Search and indexing

`ElasticSearchRequest` accepts Elasticsearch query builders directly and can also be constructed from an `ElasticSearchQuery` for composable restrictions, sorting, highlights, and aggregations. Searches support offset pagination through Jakarta Data's `PageRequest`; cursor pagination is not supported.

Use `ElasticIndexer` when mapping source entities and dispatching index or delete operations, particularly when an application-managed `Executor` should run indexing asynchronously. Mapping classes used with `ElasticIndexer` must provide an accessible no-argument constructor.

## Build and test

```bash
mvn verify
```

This runs the test suite and creates the binary, source, and JavaDoc JARs under `target/`.

## Contributing

Contributions are welcome. See [CONTRIBUTING.md](CONTRIBUTING.md) for the development workflow and pull-request expectations. For security issues, follow [SECURITY.md](SECURITY.md) rather than opening a public issue.

## License

This project is licensed under the [Apache License 2.0](LICENSE).
