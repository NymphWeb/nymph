package com.nymph.bean.impl;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.nymph.annotation.Components;
import com.nymph.bean.component.BeansComponent;
import com.nymph.interceptor.EnableInterceptor;
import com.nymph.interceptor.NyInterceptors;
import com.nymph.utils.AnnoUtil;

/**
 * bean组件的处理的实现
 * @author LiuYang
 * @author LiangTianDong
 * @date 2017年10月23日上午11:49:38
 */
public class DefaultBeansComponent implements BeansComponent {
	/**
	 *  存放组件的容器
	 */
	private final Map<Class<?>, Object> components = new HashMap<>(16);

	/**
	 *  过滤器链
	 */
	private final List<NyInterceptors> interceptors = new LinkedList<>();
	
	@Override
	public Optional<Object> getComponent(Class<? extends Annotation> anno) {
		return Optional.ofNullable(components.get(anno));
	}

	@Override
	public List<NyInterceptors> getInterceptors() {
		return interceptors;
	}

	@Override
	public void filter(Object bean) {
		Class<?> beanClass = bean.getClass();
		if (isIntercept(beanClass)) {
			interceptors.add((NyInterceptors)bean);
			return;
		}

		Annotation[] annotations = beanClass.getAnnotations();
		Class<?> anno = AnnoUtil.getType(annotations, Components.class);
		if (anno != null) {
			components.put(anno, bean);
		}
	}

	private boolean isIntercept(Class<?> bean) {
		return bean.isAnnotationPresent(EnableInterceptor.class);
	}

}
