/**
 * 
 */
package com.nymph.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
/**
 * 作用与Bean相同, 用于区分Web项目三层结构, 一般用在Service实现类上
 * @author LiuYang
 * @author LiangTianDong
 * @date 2017年10月22日下午3:09:22
 */
@Retention(RUNTIME)
@Target(TYPE)
@Bean
public @interface BIZ {

}
