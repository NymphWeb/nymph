package com.nymph.annotation;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
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
@Documented
public @interface UrlVar {

	/**
	 * 比如@GET(/demo/${test}), 在方法的形参内可以用@UrlVar("test") String xxx 
	 *     来获取到${test}的实际值, 这里的${}可以是任何符号@(), #(), $(), @{}只要符号中间
	 *     有字符串就可以正常解析
	 */
	String value() default "";
}
