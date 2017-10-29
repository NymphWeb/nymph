package com.nymph.bean;

import com.nymph.interceptor.NyInterceptors;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;

public interface BeansComponent {
	/**
	 * 根据注解类型获取组件Bean
	 * @param anno	指定的注解
	 * @return		对应的bean组件
	 */
	Optional<Object> getComponent(Class<? extends Annotation> anno);

	/**
	 * 获取拦截器链
	 * @return
	 */
	List<NyInterceptors> getInterceptors();
	/**
	 * 过滤出组件的Bean
	 * @param bean bean实例
	 */
	void filter(Object bean);

}
