package com.nymph.bean;

import java.util.Map;
import java.util.Set;

import com.nymph.bean.impl.HttpBean;

/**
 * 专门用来存放Http相关的bean的容器, url所映射的信息应该细化到到具体的类的具体的某个方法, 这样
 *     可以做到一个url对应一个method, 用hashMap来存储这些信息时可以直接获取到对应的类和方法
 *     而不用遍历从而提高了效率
 * @author NYMPH
 * @date 2017年10月5日下午1:34:33
 */
public interface HttpBeansContainer {
	/**
	 * 对拥有@HTTP注解的bean进行处理主要是将@HTTP注解的value和@Request系列注解(@GET这种)
	 *     的value拼接成一个完整的表示url地址的字符串, 然后存入map中, 这样当一个请求访问过来
	 *     时只需要拿到这个请求的uri就可以直接获取到对应的Class和Method,如果用@PlaceHolder
	 *     注解这种占位符形式的url的话就只有遍历来寻找是否存在请求所映射的类了
	 * @param clazz	待处理的Class
	 */
	void filterAlsoSave(Class<?> clazz);
	/**
	 * 根据url来获取Http的bean
	 * @param url	url地址
	 * @return		bean的实例
	 */
	HttpBean getHttpBean(String url);
	/**
	 * 获取所有的httpbean
	 * @return		bean的集合
	 */
	Set<Map.Entry<String, HttpBean>> getAllHttpBean();

}
