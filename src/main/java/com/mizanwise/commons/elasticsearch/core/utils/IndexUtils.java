package com.mizanwise.commons.elasticsearch.core.utils;

import com.mizanwise.commons.elasticsearch.core.mapping.MappingException;
import com.mizanwise.commons.elasticsearch.core.mapping.annotation.Index;
import org.apache.commons.lang3.StringUtils;

public final class IndexUtils {

    private IndexUtils() {
        throw new UnsupportedOperationException();
    }

    public static String getIndexNameWithPrefix(Class<?> clazz) throws MappingException {
        return getIndexPrefix() + getIndexName(clazz);
    }

    public static String getIndexName(Class<?> clazz) throws MappingException {
        if (!clazz.isAnnotationPresent(Index.class))
            throw new MappingException("Mapping class is not annotated with @Index");

        String indexName = clazz.getAnnotation(Index.class).value();
        if (StringUtils.isBlank(indexName))
            indexName = clazz.getSimpleName();

        return indexName;
    }

    public static String getIndexPrefix() {
        String indexPrefix = ConfigProvider.getIndexPrefix();
        if (StringUtils.isNotBlank(indexPrefix))
            indexPrefix += "_";

        return indexPrefix;
    }

    public static String appendIndexPrefix(String index) {
        String indexPrefix = getIndexPrefix();
        if (index != null && index.startsWith(indexPrefix))
            return index;

        return indexPrefix + index;
    }

}
