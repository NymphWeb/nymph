package com.nymph.bean;

import java.lang.annotation.Annotation;
import java.util.Optional;

public interface BeansComponent {
	/**
	 * 根据注解类型获取组件Bean
	 * @param anno	指定的注解
	 * @return		对应的bean组件
	 */
	Optional<Object> getComponent(Class<? extends Annotation> anno);
	/**
	 * 过滤出组件的Bean
	 * @param bean bean实例
	 */
	void filter(Object bean);

}
