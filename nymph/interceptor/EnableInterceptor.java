package com.nymph.interceptor;

import com.nymph.annotation.Bean;
import com.nymph.annotation.Components;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(TYPE)
@Components
@Bean
public @interface EnableInterceptor {
}
