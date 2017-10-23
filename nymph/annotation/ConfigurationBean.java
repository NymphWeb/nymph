package com.nymph.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
/**
 * 配置bean 标注了这个注解的类会被进行一些处理 --> 调用所有被@Bean注解标注的方法
 *     并且将返回值注册到Bean工厂
 * @date 2017年10月22日下午3:14:43
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface ConfigurationBean {

}
