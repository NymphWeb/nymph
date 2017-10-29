package com.nymph.exception.handle;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.nymph.annotation.Bean;
import com.nymph.annotation.Components;
/**
 * 标识了此注解的类会被当做异常处理器(必须实现ExceptionHandler接口)
 * @author LiuYang, LiangTianDong
 * @date 2017年10月29日下午9:33:47
 */
@Retention(RUNTIME)
@Target(TYPE)
@Components
@Bean
public @interface EnableExceptionHandler {

}
