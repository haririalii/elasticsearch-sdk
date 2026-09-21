package com.mizanwise.commons.elasticsearch.core.utils;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.mizanwise.commons.elasticsearch.core.mapping.annotation.Property;
import org.apache.commons.lang3.StringUtils;

public class ElasticsearchNamingStrategy extends PropertyNamingStrategy {

    public static final ElasticsearchNamingStrategy INSTANCE = new ElasticsearchNamingStrategy();

    @Override
    public String nameForField(MapperConfig<?> config, AnnotatedField field, String defaultName) {
        Property property = field.getAnnotation(Property.class);
        if (property != null && StringUtils.isNotBlank(property.value()))
            return property.value();

        return super.nameForField(config, field, defaultName);
    }

    @Override
    public String nameForGetterMethod(MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
        Property property = method.getAnnotation(Property.class);
        if (property != null && StringUtils.isNotBlank(property.value()))
            return property.value();

        return super.nameForGetterMethod(config, method, defaultName);
    }

    @Override
    public String nameForSetterMethod(MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
        Property property = method.getAnnotation(Property.class);
        if (property != null && StringUtils.isNotBlank(property.value()))
            return property.value();

        return super.nameForSetterMethod(config, method, defaultName);
    }

    @Override
    public String nameForConstructorParameter(MapperConfig<?> config, AnnotatedParameter ctorParam, String defaultName) {
        return super.nameForConstructorParameter(config, ctorParam, defaultName);
    }
}
