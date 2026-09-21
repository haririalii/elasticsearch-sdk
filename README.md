# Elasticsearch SDK

A framework-neutral Java library for creating Elasticsearch mappings, queries, and REST-client operations. It is
intended for CDI-managed Jakarta EE and Quarkus applications, while keeping framework annotations out of its public API.

> This release uses the final 7.17 Java High Level REST Client release (7.17.29). Elastic's supported 9.x Java API
> client is not source-compatible with this library; adopting it requires a major-version API migration.

## Features

- Annotation-driven Elasticsearch mapping generation
- Composable search criteria, sorting, highlighting, and aggregations
- Synchronous and asynchronous index operations
- A DAO/service boundary suitable for CDI application services

## Requirements

- Java 25 or newer
- Elasticsearch 7.17.29 REST API
- Maven 3.9 or newer

## Build and verify

```bash
mvn verify
```

This runs the test suite and produces binary, source, and JavaDoc JARs in `target/`.

## Using the library locally

Until the artifact is published to a Maven repository, install it to your local repository:

```bash
mvn install
```

```xml
<dependency>
    <groupId>com.mizanwise.commons</groupId>
    <artifactId>elasticsearch-sdk</artifactId>
    <version>2.0.0-SNAPSHOT</version>
</dependency>
```

## Jakarta EE integration

Create one `RestHighLevelClient` during application startup, provide it to an application-scoped DAO, and close it
during shutdown. Do not create a client per request.

```java
@ApplicationScoped
public class ProductSearchDao extends AbstractElasticsearchDAO<ProductDocument> {
    public ProductSearchDao(RestHighLevelClient client) {
        super(client);
    }

    @Override
    public Class<ProductDocument> getMappingClass() {
        return ProductDocument.class;
    }
}
```

The library uses SLF4J only; configure its logging implementation in the consuming application. Runtime helpers read JVM
system properties:

| Property                             | Meaning                             | Default |
|--------------------------------------|-------------------------------------|---------|
| `elasticsearch.enable`               | Enables index and search operations | `false` |
| `elasticsearch.endpoints`            | Comma-separated endpoint URLs       | empty   |
| `elasticsearch.index_prefix`         | Prefix prepended to index names     | empty   |

## Contributing

Contributions are welcome. Please read [CONTRIBUTING.md](CONTRIBUTING.md) before opening an issue or pull request.
Security issues must follow [SECURITY.md](SECURITY.md).

## License

A license must be selected and added before publishing the project as open source. Do not publish a public repository
without one.
