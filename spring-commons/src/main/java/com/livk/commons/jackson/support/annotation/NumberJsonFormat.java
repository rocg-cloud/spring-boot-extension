package com.livk.commons.jackson.support.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.livk.commons.jackson.support.NumberJsonSerializer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author livk
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@JacksonAnnotationsInside
@JsonSerialize(using = NumberJsonSerializer.class)
public @interface NumberJsonFormat {

    String pattern() default "#0.00";

    boolean simpleTypeSupport() default true;
}