package com.mizanwise.commons.elasticsearch.core.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Utility in association with json entities
 */

/**
 * JSON conversion utilities using the library's Elasticsearch field naming strategy.
 *
 * <p>The exposed mapper is shared and should be treated as immutable after application startup.</p>
 */
public final class JsonUtils {

    public static final ObjectMapper MAPPER = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .setPropertyNamingStrategy(ElasticsearchNamingStrategy.INSTANCE);

    private JsonUtils() {
    }

    /**
     * Converts an object to a JSON tree.
     */
    public static JsonNode convert(Object object) {
        return MAPPER.valueToTree(object);
    }

    /**
     * Parses JSON text into a JSON tree.
     */
    public static JsonNode convert(String json) throws IOException {
        return MAPPER.readTree(json);
    }

    public static String convertToString(Object object) {
        try {
            return MAPPER.writeValueAsString(object);
        } catch (IOException exception) {
            throw new IllegalArgumentException("Unable to serialize object as JSON", exception);
        }
    }

    /**
     * Converts an object to the requested target type using the configured mapper.
     */
    public static <E> E convert(Object object, Class<E> clazz) {
        return MAPPER.convertValue(object, clazz);
    }

}
