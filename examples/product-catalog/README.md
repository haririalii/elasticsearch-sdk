# Product catalog example

This Quarkus application demonstrates the complete SDK flow: define a mapping, create an index mapping, index documents, and run a full-text search through REST endpoints.

## Prerequisites

- Java 25+
- Maven 3.9+
- Docker (for the local Elasticsearch node)

## Run it

From the repository root, first install the SDK into your local Maven repository:

```bash
mvn install
```

Start Elasticsearch, then run the example:

```bash
cd examples/product-catalog
docker compose up -d
mvn -Delasticsearch.enable=true quarkus:dev
```

In a second terminal, seed the catalog and search it:

```bash
curl -X POST http://localhost:8080/products/seed
curl 'http://localhost:8080/products?q=wireless%20keyboard'
```

The seed endpoint creates the `products` mapping and indexes three products. Re-running it updates the same documents.

Stop the local service when finished:

```bash
docker compose down
```

`compose.yaml` deliberately disables security for a local-only, zero-configuration demonstration. Supply credentials to `RestClientFactory.create` in real deployments when your cluster requires authentication.
