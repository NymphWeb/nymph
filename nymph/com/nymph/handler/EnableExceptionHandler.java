package com.nymph.handler;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.nymph.annotation.Bean;
import com.nymph.annotation.Components;

@Retention(RUNTIME)
@Target(TYPE)
@Components
@Bean
public @interface EnableExceptionHandler {

}
