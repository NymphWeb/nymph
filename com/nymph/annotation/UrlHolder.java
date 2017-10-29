package com.nymph.annotation;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
/**
 * 声明一个与url对应的变量占位符
 * @author LiuYang
 * @author LiangTianDong
 * @date 2017年10月3日下午1:39:32
 */
@Retention(RUNTIME)
@Target(PARAMETER)
@Inherited
public @interface UrlHolder {

	/**
	 * 比如/demo/@test, 在方法的形参内可以用@UrlHolder("test") String xxx
	 *     来获取到@test的实际值, 如果形参名和声明的变量名字一样可以不需要指定,如@UrlHolder String test
	 */
	String value() default "";
}
