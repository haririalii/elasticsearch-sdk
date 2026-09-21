package com.mizanwise.commons.elasticsearch.core.mapping.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Text {
    String analyzer() default "";

    String searchAnalyzer() default "";

    String tokenCount() default "";

    String termVector() default "";

}
