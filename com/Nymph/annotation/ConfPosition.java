package com.nymph.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target(TYPE)
/**
 * @comment 定位配置文件的位置
 * @author liu
 * @author liang
 * @date 2017年9月27日下午3:21:06
 */
public @interface ConfPosition {
	/** 配置文件的路径, 可以配置多个 */
	String[] value();
}