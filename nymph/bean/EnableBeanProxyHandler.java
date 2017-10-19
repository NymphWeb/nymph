package com.nymph.bean;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.nymph.annotation.Bean;
import com.nymph.annotation.Components;

@Retention(RUNTIME)
@Target(TYPE)
@Bean
@Components
public @interface EnableBeanProxyHandler {

}
