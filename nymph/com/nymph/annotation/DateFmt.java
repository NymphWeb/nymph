package com.nymph.annotation;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
/**
 * 指定时间的格式
 * @author LiuYang
 * @author LiangTianDong
 * @date 2017年9月29日下午5:21:21
 */
@Retention(RUNTIME)
@Target({ PARAMETER })
@Documented
public @interface DateFmt {

	/**
	 * 格式参照SimpleDateFormat类的格式, 像yyyy-MM-dd这种 或者其他
	 * @return
	 */
	String value() default "";
}
