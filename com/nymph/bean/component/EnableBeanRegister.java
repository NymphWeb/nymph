package com.nymph.bean.component;

import com.nymph.annotation.Bean;
import com.nymph.annotation.Components;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Bean
@Components
public @interface EnableBeanRegister {
}
