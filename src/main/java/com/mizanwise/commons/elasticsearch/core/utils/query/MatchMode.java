package com.mizanwise.commons.elasticsearch.core.utils.query;

public enum MatchMode {

    EXACT {
        @Override
        public String toWildcardString(String pattern) {
            return pattern;
        }
    },

    /**
     * Match the start of the string to the pattern
     */
    START {
        @Override
        public String toWildcardString(String pattern) {
            return pattern + '*';
        }
    },

    /**
     * Match the end of the string to the pattern
     */
    END {
        @Override
        public String toWildcardString(String pattern) {
            return '*' + pattern;
        }
    },

    /**
     * Match the pattern anywhere in the string
     */
    ANYWHERE {
        @Override
        public String toWildcardString(String pattern) {
            return '*' + pattern + '*';
        }
    };

    /**
     * Convert the pattern, by appending/prepending "*"
     *
     * @param pattern The pattern for convert according to the mode
     * @return The converted pattern
     */
    public abstract String toWildcardString(String pattern);

}
