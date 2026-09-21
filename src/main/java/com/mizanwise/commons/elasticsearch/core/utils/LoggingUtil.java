package com.mizanwise.commons.elasticsearch.core.utils;

import org.slf4j.Logger;

public final class LoggingUtil {

    private static final String MSG__ELASTICSEARCH_DISABLED = "elasticsearch is disabled! you can enable it using " + ConfigProvider.PROP_CONFIG_ENABLE;

    private LoggingUtil() {
        throw new UnsupportedOperationException();
    }

    public static void logElasticsearchDisabled_debug(Logger logger) {
        debug(logger, MSG__ELASTICSEARCH_DISABLED);
    }

    private static void debug(Logger logger, String msg) {
        if (logger.isDebugEnabled())
            logger.debug(msg);
    }

}
