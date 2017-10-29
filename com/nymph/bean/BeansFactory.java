package com.nymph.bean;

import java.util.Collection;

/**
 * bean工厂接口
 * @author NYMPH
 * @date 2017年10月3日下午2:18:41
 */
public interface BeansFactory {
	/**
	 * 将bean注册到容器中
	 */
	void register();
	/**
	 * 根据bean的名字获取到bean的实例
	 * @param beanName	bean的名字, 一般为bean的类名
	 * @return			这个bean的实例
	 */
	Object getBean(String beanName);
	/**
	 * 根据bean的类型来获取到bean的实例
	 * @param beanClass bean的类型
	 * @return			bean的实例
	 */
	<T> T getBean(Class<T> beanClass);
	/**
	 * 获取bean工厂的所有bean实例
	 * @return bean实例的集合
	 */
	Collection<?> getBeans();
}
