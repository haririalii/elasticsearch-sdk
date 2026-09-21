package com.mizanwise.commons.elasticsearch.core.mapping;

import com.mizanwise.commons.elasticsearch.core.mapping.annotation.*;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.geo.Point;
import org.elasticsearch.xcontent.XContentBuilder;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Mapping utilities
 */

/**
 * Creates Elasticsearch mapping definitions from the library's mapping annotations.
 */
public final class MappingUtils {

    public static final String FIELD_PROPERTIES = "properties";
    public static final String FIELD_FIELDS = "fields";
    public static final String FIELD_TYPE = "type";
    public static final String FIELD_ANALYZER = "analyzer";
    public static final String FIELD_SEARCH_ANALYZER = "search_analyzer";
    public static final String FIELD_FORMAT = "format";
    public static final String FIELD_IGNORE_ABOVE = "ignore_above";
    public static final String FIELD_NUMERIC_DETECTION = "numeric_detection";
    public static final String FIELD_TERM_VECTOR = "term_vector";
    public static final String TYPE_TEXT = "text";
    public static final String TYPE_NESTED = "nested";
    public static final String TYPE_KEYWORD = "keyword";
    public static final String TYPE_DATE = "date";
    public static final String TYPE_IP = "ip";
    public static final String TYPE_VERSION = "version";
    public static final String TYPE_POINT = "point";
    public static final String TYPE_TOKEN_COUNT = "token_count";
    public static final boolean NUMERIC_DETECTION = true;
    public static final Integer IGNORE_ABOVE = 256;
    private static final Map<Class<?>, String> primitiveTypeMap = new HashMap<>();

    static {
        primitiveTypeMap.put(Boolean.TYPE, "boolean");
        primitiveTypeMap.put(Byte.TYPE, "byte");
        primitiveTypeMap.put(Short.TYPE, "short");
        primitiveTypeMap.put(Integer.TYPE, "integer");
        primitiveTypeMap.put(Long.TYPE, "long");
        primitiveTypeMap.put(Double.TYPE, "double");
        primitiveTypeMap.put(Float.TYPE, "float");
    }
    private MappingUtils() {
    }

    /**
     * This method create elasticsearch mapping json from annotated class
     *
     * @param mappingClass    mapping class
     * @param xContentBuilder xContentBuilder
     * @throws MappingException Mapping creation error
     */
    public static void createMapping(Class<?> mappingClass, String defaultAnalyzer, XContentBuilder xContentBuilder) throws MappingException {
        if (!mappingClass.isAnnotationPresent(Index.class) || mappingClass.isAnnotationPresent(MappingIgnore.class))
            throw new MappingException(String.format("%s don't have @Index annotation!", mappingClass.getName()));

        try {
            xContentBuilder.startObject();
            processClass(mappingClass, xContentBuilder);
            builderTemplate(xContentBuilder, defaultAnalyzer);
//                xContentBuilder.field(FIELD_NUMERIC_DETECTION, NUMERIC_DETECTION);
            xContentBuilder.endObject();
        } catch (IOException e) {
            throw new MappingException(e);
        }
    }

    /**
     * Process classes annotations
     *
     * @param mappingClass    a mapping class
     * @param xContentBuilder XContent builder
     * @throws IOException Mapping creation error
     */
    private static void processClass(Class<?> mappingClass, XContentBuilder xContentBuilder) throws IOException {
        xContentBuilder.startObject(FIELD_PROPERTIES);
        Class<?> mapping = mappingClass;
        do {
            Field[] fields = mapping.getDeclaredFields();

            for (Field field : fields)
                processField(field, xContentBuilder);

            mapping = mapping.getSuperclass();
        } while (mapping != null);
        xContentBuilder.endObject();
    }

    private static void builderTemplate(XContentBuilder dynamicTemplate, String defaultAnalyzer) throws IOException {
        dynamicTemplate.startArray("dynamic_templates");
        {
            dynamicTemplate.startObject();
            {
                dynamicTemplate.startObject("strings_keyword_field");
                {
                    dynamicTemplate.field("match", "*");
                    dynamicTemplate.field("match_mapping_type", "string");
                    dynamicTemplate.startObject("mapping");
                    {
                        if (StringUtils.isNotBlank(defaultAnalyzer))
                            dynamicTemplate.field("analyzer", defaultAnalyzer);

                        dynamicTemplate.startObject("fields");
                        {
                            dynamicTemplate.startObject("keyword");
                            {
                                dynamicTemplate.field("ignore_above", 512);
                                dynamicTemplate.field("type", "keyword");
                            }
                            dynamicTemplate.endObject();
                        }
                        dynamicTemplate.endObject();
                    }
                    dynamicTemplate.endObject();
                }
                dynamicTemplate.endObject();
            }
            dynamicTemplate.endObject();
        }
        dynamicTemplate.endArray();
    }

    /**
     * Process fields annotations
     * we don't need process primitive field due to elasticsearch dynamic mapping
     *
     * @param field           class field
     * @param xContentBuilder xContentBuilder
     * @throws IOException create mapping error
     */
    private static void processField(Field field, XContentBuilder xContentBuilder) throws IOException {
        if (!field.isAnnotationPresent(Property.class))
            return;

        if (field.isAnnotationPresent(Nested.class)) {
            nested(field, xContentBuilder);
        } else if (field.isAnnotationPresent(Text.class)) {
            text(field, xContentBuilder);
        } else if (field.isAnnotationPresent(Version.class)) {
            version(field, xContentBuilder);
        } else if (field.getType().equals(InetAddress.class)) {
            ip(field, xContentBuilder);
        } else if (field.getType().equals(Point.class)) {
            point(field, xContentBuilder);
        } else if (field.getType().equals(String.class) || field.getType().isEnum()) {
            keyword(field, xContentBuilder);
        } else if (field.isAnnotationPresent(Date.class) || field.getType().equals(java.util.Date.class)) {
            date(field, xContentBuilder);
        } else if (ClassUtils.isPrimitiveOrWrapper(field.getType())) {
            primitive(field, xContentBuilder);
        } else if (Collection.class.isAssignableFrom(field.getType()) && field.isAnnotationPresent(Keyword.class)) {
            keyword(field, xContentBuilder);
        } else {
            object(field, xContentBuilder);
        }
    }

    private static void nested(Field field, XContentBuilder xContentBuilder) throws IOException {
        object(field, true, xContentBuilder);
    }

    private static void object(Field field, XContentBuilder xContentBuilder) throws IOException {
        object(field, false, xContentBuilder);
    }

    private static void object(Field field, boolean nested, XContentBuilder xContentBuilder) throws IOException {
        Class<?> type;
        if (Collection.class.isAssignableFrom(field.getType())) {
            ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
            Type actualTypeArgument = parameterizedType.getActualTypeArguments()[0];
            if (actualTypeArgument instanceof Class)
                type = (Class<?>) actualTypeArgument;
            else {
                ParameterizedType actualTypeArgument_internal = (ParameterizedType) actualTypeArgument;
                type = (Class<?>) actualTypeArgument_internal.getRawType();
            }
        } else
            type = field.getType();

        xContentBuilder.startObject(name(field));
        if (nested)
            xContentBuilder.field(FIELD_TYPE, TYPE_NESTED);
        processClass(type, xContentBuilder);
        xContentBuilder.endObject();
    }

    private static void keyword(Field field, XContentBuilder xContentBuilder) throws IOException {
        xContentBuilder.startObject(name(field));
        xContentBuilder.field(FIELD_TYPE, TYPE_KEYWORD);
        xContentBuilder.endObject();
    }

    private static void date(Field field, XContentBuilder xContentBuilder) throws IOException {
        xContentBuilder.startObject(name(field));
        xContentBuilder.field(FIELD_TYPE, TYPE_DATE);

        Date date = field.getAnnotation(Date.class);
        if (date != null && StringUtils.isNotBlank(date.format()))
            xContentBuilder.field(FIELD_FORMAT, date.format());

        xContentBuilder.endObject();
    }

    private static void text(Field field, XContentBuilder xContentBuilder) throws IOException {
        Text text = field.getAnnotation(Text.class);

        xContentBuilder.startObject(name(field));
        xContentBuilder.field(FIELD_TYPE, TYPE_TEXT);
        if (StringUtils.isNotBlank(text.analyzer())) {
            xContentBuilder.field(FIELD_ANALYZER, text.analyzer());
            if (StringUtils.isNotBlank(text.tokenCount())) {
                xContentBuilder.startObject(text.tokenCount());
                xContentBuilder.field(FIELD_TYPE, TYPE_TOKEN_COUNT);
                xContentBuilder.field(FIELD_ANALYZER, text.analyzer());
                xContentBuilder.endObject();
            }
        }
        if (StringUtils.isNotBlank(text.searchAnalyzer()))
            xContentBuilder.field(FIELD_SEARCH_ANALYZER, text.searchAnalyzer());

        if (StringUtils.isNotBlank(text.termVector()))
            xContentBuilder.field(FIELD_TERM_VECTOR, text.termVector());

        xContentBuilder.startObject(FIELD_FIELDS);
        xContentBuilder.startObject(TYPE_KEYWORD);
        xContentBuilder.field(FIELD_TYPE, TYPE_KEYWORD);
        xContentBuilder.field(FIELD_IGNORE_ABOVE, IGNORE_ABOVE);
        xContentBuilder.endObject();
        xContentBuilder.endObject();

        xContentBuilder.endObject();
    }

    private static void ip(Field field, XContentBuilder xContentBuilder) throws IOException {
        xContentBuilder.startObject(name(field));
        xContentBuilder.field(FIELD_TYPE, TYPE_IP);
        xContentBuilder.endObject();
    }

    private static void version(Field field, XContentBuilder xContentBuilder) throws IOException {
        xContentBuilder.startObject(name(field));
        xContentBuilder.field(FIELD_TYPE, TYPE_VERSION);
        xContentBuilder.endObject();
    }

    private static void point(Field field, XContentBuilder xContentBuilder) throws IOException {
        xContentBuilder.startObject(name(field));
        xContentBuilder.field(FIELD_TYPE, TYPE_POINT);
        xContentBuilder.endObject();
    }

    private static void primitive(Field field, XContentBuilder xContentBuilder) throws IOException {
        String type = getPrimitiveType(getPrimitiveClass(field.getType()));
        if (type == null)
            throw new MappingException(String.format(
                    "Primitive type not supported! field: %s , type: %s",
                    field.getName(),
                    field.getType().getSimpleName()
            ));

        xContentBuilder.startObject(name(field));
        xContentBuilder.field(FIELD_TYPE, type);
        xContentBuilder.endObject();
    }

    private static String getPrimitiveType(Class<?> clazz) {
        if (!primitiveTypeMap.containsKey(clazz))
            return null;

        return primitiveTypeMap.get(clazz);
    }

    private static Class<?> getPrimitiveClass(Class<?> clazz) {
        if (ClassUtils.isPrimitiveWrapper(clazz))
            return ClassUtils.wrapperToPrimitive(clazz);

        return clazz;
    }

    private static String name(Field field) {
        Property property = field.getAnnotation(Property.class);
        return StringUtils.isNoneBlank(property.value()) ? property.value() : field.getName();
    }

}
