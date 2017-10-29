package com.nymph.interceptor;

import com.nymph.annotation.Bean;
import com.nymph.annotation.Components;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
/**
 * 标注了此注解的类会被当做拦截器使用, 前提是必须实现NyInterceptors
 * @author LiuYang
 * @author LiangTianDong
 * @date 2017年10月23日下午12:42:13
 */
@Retention(RUNTIME)
@Target(TYPE)
@Components
@Bean
public @interface EnableInterceptor {
}
