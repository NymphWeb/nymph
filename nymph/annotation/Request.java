/**
 * 
 */
package com.nymph.annotation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
/**
 * 用于标记4种请求方式
 * @author LiuYang
 * @author LiangTianDong
 * @date 2017年10月22日下午3:21:49
 */
@Retention(RUNTIME)
@Target({ METHOD, ANNOTATION_TYPE })
public @interface Request {
	
}
