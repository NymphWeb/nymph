package com.nymph.json;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 指定被解析字段的名字
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface Name {
	
	String value() default "";

}
