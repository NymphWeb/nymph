package com.nymph.json;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 忽略解析的字段
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface Exclude {

}
