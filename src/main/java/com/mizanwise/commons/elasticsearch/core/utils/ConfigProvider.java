package com.mizanwise.commons.elasticsearch.core.utils;

import com.mizanwise.commons.elasticsearch.core.exception.ElasticsearchDisabledException;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Reads optional library configuration from JVM system properties.
 */
public final class ConfigProvider {
    public static final String PROP_CONFIG_ENABLE = "elasticsearch.enable";
    public static final String PROP_CONFIG_ENDPOINTS = "elasticsearch.endpoints";
    public static final String PROP_CONFIG_USERNAME = "elasticsearch.username";
    public static final String PROP_CONFIG_PASSWORD = "elasticsearch.password";
    public static final String PROP_CONFIG_INDEX_PREFIX = "elasticsearch.index_prefix";
    public static final String PROP_CONFIG_MAPPING_CREATOR = "elasticsearch.mapping.creator.enabled";

    private ConfigProvider() {
    }

    public static boolean isDisable() {
        return !isEnable();
    }

    public static boolean isEnable() {
        return Boolean.parseBoolean(property(PROP_CONFIG_ENABLE, "false"));
    }

    public static Collection<String> getEndpoints() {
        String value = property(PROP_CONFIG_ENDPOINTS, "");
        return value.isBlank() ? List.of() : Arrays.stream(value.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList();
    }

    public static String getIndexPrefix() {
        return property(PROP_CONFIG_INDEX_PREFIX, "");
    }

    public static void checkDisabled() {
        if (isDisable()) throw new ElasticsearchDisabledException();
    }

    private static String property(String key, String defaultValue) {
        return System.getProperty(key, defaultValue);
    }
}
