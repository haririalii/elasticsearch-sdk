package com.mizanwise.commons.elasticsearch.core.utils.query;

import com.mizanwise.commons.elasticsearch.core.mapping.MappingUtils;
import com.mizanwise.commons.elasticsearch.core.query.restriction.QueryComposition;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class ElasticQueryBuilders {

    private ElasticQueryBuilders() {
        throw new UnsupportedOperationException();
    }

    // like
    public static QueryBuilder likeQuery(String fieldName, String value) {
        return likeQuery(fieldName, value, null);
    }

    public static QueryBuilder likeQuery(String fieldName, String value, MatchMode matchMode) {
        if (matchMode == null)
            matchMode = MatchMode.START;

        return QueryBuilders.disMaxQuery()
                .add(termQuery_keyword(fieldName, value).boost(4f))
                .add(wildcard_queryString(fieldName, value, matchMode).boost(3f))
                .tieBreaker(0.7f);
    }

    public static QueryBuilder likeQuery(String fieldName, Collection<String> values, QueryComposition composition) {
        return likeQuery(fieldName, values, composition, null);
    }

    public static QueryBuilder likeQuery(String fieldName, Collection<String> values, QueryComposition composition, MatchMode matchMode) {
        return boolQuery(
                composition,
                values,
                value -> likeQuery(fieldName, value, matchMode)
        );
    }

    // full text
    public static QueryBuilder fullTextQuery(String field, String value) {
        return fullTextQuery(field, value, (String) null);
    }

    public static QueryBuilder fullTextQuery(String field, String value, MatchMode matchMode) {
        return fullTextQuery(field, value, null, Fuzziness.AUTO, matchMode);
    }

    public static QueryBuilder fullTextQuery(String field, String value, String analyzer) {
        return fullTextQuery(field, value, analyzer, Fuzziness.AUTO, MatchMode.START);
    }

    public static QueryBuilder fullTextQuery(String field, String value, Fuzziness fuzziness) {
        return fullTextQuery(field, value, null, fuzziness, MatchMode.START);
    }

    public static QueryBuilder fullTextQuery(String field, String value, String analyzer, Fuzziness fuzziness, MatchMode matchMode) {
        return QueryBuilders.disMaxQuery()
                .add(termQuery_keyword(field, value).boost(4F))
                .add(wildcard_queryString(field, value, analyzer, matchMode).boost(3F))
                .add(matchQuery(field, value, analyzer, fuzziness).boost(2F))
//                .add(wildcard_queryString(field, value, analyzer, MatchMode.END).boost(2F))
//                .add(wildcard_queryString(field, value, analyzer, MatchMode.ANYWHERE).boost(1F))
                .tieBreaker(0.8F);
    }

    public static QueryBuilder fullTextQuery(String field, Collection<String> values, QueryComposition composition) {
        return boolQuery(
                composition,
                values,
                value -> fullTextQuery(field, value)
        );
    }

    public static QueryBuilder fullTextQuery(String field, Fuzziness fuzziness, Collection<String> values, QueryComposition composition) {
        return boolQuery(
                composition,
                values,
                value -> fullTextQuery(field, value, null, fuzziness, MatchMode.START)
        );
    }

    // match query
    public static MatchQueryBuilder matchQuery(String field, String value, String analyzer, Fuzziness fuzziness) {
        return QueryBuilders.matchQuery(field, value)
                .analyzer(analyzer)
                .fuzziness(fuzziness)
                .maxExpansions(5);
    }

    // wildcard
    public static QueryBuilder wildcardQuery(String fieldName, String value) {
        return keywordExistenceQuery(
                fieldName,
                filed -> QueryBuilders.wildcardQuery(filed, value)
        );
    }

    public static QueryBuilder wildcardQuery(String fieldName, Collection<String> values, QueryComposition composition) {
        return boolQuery(
                composition,
                values,
                value -> wildcardQuery(fieldName, value)
        );
    }

    public static QueryBuilder wildcard_queryString(String field, String query) {
        return wildcard_queryString(field, query, (String) null);
    }

    public static QueryBuilder wildcard_queryString(String field, String query, MatchMode matchMode) {
        return wildcard_queryString(field, query, null, matchMode);
    }

    public static QueryBuilder wildcard_queryString(String field, Collection<String> values, QueryComposition composition) {
        return boolQuery(
                composition,
                values,
                value -> wildcard_queryString(field, value)
        );
    }

    public static QueryBuilder wildcard_queryString(String fieldName, String query, String analyzer) {
        return wildcard_queryString(fieldName, query, analyzer, MatchMode.EXACT);
    }

    public static QueryBuilder wildcard_queryString(String fieldName, String query, String analyzer, MatchMode matchMode) {
        // escape input query
        query = QueryParser.escape(query);
        // apply match mode wildcard
        if (matchMode != null)
            query = matchMode.toWildcardString(query);

        final String finalQuery = query;
        return QueryBuilders.queryStringQuery(finalQuery)
                .field(fieldName)
                .analyzeWildcard(true)
                .analyzer(analyzer);
    }

    public static QueryBuilder wildcardOnKeyword_queryString(String fieldName, String query, String analyzer, MatchMode matchMode) {
        // escape input query
        query = QueryParser.escape(query);
        // apply match mode wildcard
        if (matchMode != null)
            query = matchMode.toWildcardString(query);

        final String finalQuery = query;
        return keywordExistenceQuery(
                fieldName,
                field -> QueryBuilders.queryStringQuery(finalQuery)
                        .field(field)
                        .analyzeWildcard(true)
                        .analyzer(analyzer)
        );
    }

    // term
    public static QueryBuilder termQuery(String fieldName, Object value) {
        return QueryBuilders.termQuery(fieldName, value);
    }

    public static QueryBuilder termQuery_keyword(String fieldName, Object value) {
        return keywordExistenceQuery(
                fieldName,
                field -> QueryBuilders.termQuery(field, value)
        );
    }

    // terms
    public static QueryBuilder termsQuery(String fieldName, Collection<String> values) {
        return keywordExistenceQuery(
                fieldName,
                field -> QueryBuilders.termsQuery(field, values)
        );
    }

    // regex
    public static QueryBuilder regexpQuery(String fieldName, String regex) {
        return keywordExistenceQuery(
                fieldName,
                field -> QueryBuilders.regexpQuery(field, regex)
        );
    }

    // composite

    /**
     * @param queryMapper value to query mapper
     */
    public static QueryBuilder boolQuery(QueryComposition composition, Collection<String> values, Function<String, QueryBuilder> queryMapper) {
        return boolQuery(
                composition,
                values.stream()
                        .map(queryMapper)
                        .collect(Collectors.toList())
        );
    }

    public static QueryBuilder boolQuery(QueryComposition composition, Collection<QueryBuilder> queries) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        switch (composition) {
            case AND:
                queries.forEach(
                        boolQuery::must
                );
                break;
            case OR:
                queries.forEach(
                        boolQuery::should
                );
                break;
            case NOT:
                queries.forEach(
                        boolQuery::mustNot
                );
                break;
            default:
                throw new IllegalArgumentException("QueryComposition is not valid" + composition);
        }

/*        if (this.minimumShouldMatch != null && CollectionUtils.isNotEmpty(boolQuery.should()))
            boolQuery.minimumShouldMatch(minimumShouldMatch);*/

        return boolQuery;
    }

    /**
     * @param queryBuilder field name to query mapper
     */
    public static QueryBuilder keywordExistenceQuery(String fieldName, Function<String, QueryBuilder> queryBuilder) {
        String propKeyword = appendKeyword(fieldName);
        return QueryBuilders.boolQuery()
                .should(
                        QueryBuilders.boolQuery()
                                .must(queryBuilder.apply(fieldName))
                                .mustNot(QueryBuilders.existsQuery(propKeyword))
                )
                .should(
                        QueryBuilders.boolQuery()
                                .must(queryBuilder.apply(propKeyword))
                                .filter(QueryBuilders.existsQuery(propKeyword))
                );
    }

    public static String appendKeyword(String fieldName) {
        return fieldName + "." + MappingUtils.TYPE_KEYWORD;
    }

}
