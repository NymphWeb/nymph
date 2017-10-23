package com.nymph.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
/**
 * 标注用, 用于区分Bean的作用	
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface Components {

}
