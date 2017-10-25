package com.nymph.annotation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
/**
 * 
 * 标注了次注解的类表示将此类的实例交给容器托管	
 * @author LiuYang
 * @author LiangTianDong
 * @date 2017年10月21日下午5:18:32
 */
@Retention(RUNTIME)
@Target({TYPE, ANNOTATION_TYPE, METHOD})
public @interface Bean {
	
}
