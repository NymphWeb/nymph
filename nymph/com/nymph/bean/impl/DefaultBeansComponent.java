package com.nymph.bean.impl;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.nymph.annotation.Components;
import com.nymph.bean.BeansComponent;
import com.nymph.utils.BasicUtils;

public class DefaultBeansComponent implements BeansComponent {
	/**
	 *  存放组件的容器
	 */
	private final Map<Class<? extends Annotation>, Object> components = new HashMap<>(16);
	
	@Override
	public Optional<Object> getComponent(Class<? extends Annotation> anno) {
		return Optional.ofNullable(components.get(anno));
	}

	@Override
	public void filter(Object bean) {
		Class<? extends Annotation> anno = null;
		
		if ((anno = BasicUtils.getAnnoType(
			    bean.getClass().getAnnotations(), Components.class)) != null) {
			
			components.put(anno, bean);
		}
	}

}
