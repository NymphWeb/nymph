package com.nymph.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
/**
 * 此注解表示将当前方法返回的对象序列化到Http请求头中
 * @author LiuYang, LiangTianDong
 * @date 2017年10月31日下午5:47:13
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface Serialize {

}
