package com.nymph.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * Http协议的Get请求方式
 * @author LiuYang
 * @author LiangTianDong
 * @date 2017年10月22日下午3:18:25
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Request
public @interface GET {

	/**
	 * url路径, 应该以/开头
	 * @return
	 */
	String value() default "";
}
