package com.nymph.bean.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nymph.annotation.Injection;
import com.nymph.bean.BeansProxyHandler;

/**
 * 注入bean中被@Injection注解标识的字段或方法	
 * @author NYMPH
 * @date 2017年10月3日下午2:37:44
 */
public class BeanPropertyValueInject {
	
	private static final Logger LOG = LoggerFactory.getLogger(BeanPropertyValueInject.class);
	
	private Map<String, Object> beanContainer;
	
	private Map<String, Boolean> signs;
	
	private BeansProxyHandler proxyHandler;
	
	public void injection() {
		try {
			fieldInjection();
			setterInjection();
			constructorInjection();
		} catch (Exception e) {
			LOG.error("@Injection Exception 注入异常", e);
		}
	}
	
	public BeanPropertyValueInject(Map<String, Object> beanContainer) {
		this.beanContainer = beanContainer;
		this.signs = new HashMap<>(beanContainer.size());
	}
	
	public void setBeansProxyHandler(BeansProxyHandler proxyHandler) {
		this.proxyHandler = proxyHandler;
	}
	
	/**
	 * 关于动态代理的处理
	 * @param target	被处理的对象
	 * @throws Exception
	 */
	public void proxyHandler(Object target, String name) throws Exception {
		Field[] fields = target.getClass().getDeclaredFields();
		for (Field field : fields) {
			if (field.isAnnotationPresent(Injection.class)) {
				Object inject = findBean(field.getType(), beanContainer);
				field.setAccessible(true);
				field.set(target, inject);
				if (inject != null)
					LOG.info("field inject [{}] -> [{}]", name, field);
			}
		}
	}
	
	/**
	 * bean的字段注入(当发现@Injection注解时)
	 * @throws Exception
	 */
	public void fieldInjection() throws Exception {
		for (Entry<String, Object> entry : beanContainer.entrySet()) {
			Object bean = entry.getValue();
			String beanName = entry.getKey();
			
			Field[] fields = bean.getClass().getDeclaredFields();
			
			for (Field field : fields) {
				if (!field.isAnnotationPresent(Injection.class)) 
					continue;
				
				Object inject = findBean(field.getType(), beanContainer);
				if (inject == null) 
					continue;
					
				Object proxyBean = proxyHandler.proxyBean(inject);
				String proxyName = inject.getClass().getName();
				if (signs.get(proxyName) == null && proxyBean != inject) {
					proxyHandler(inject, proxyName);
					signs.put(proxyName, true);
				}
				
				if (null == signs.get(beanName)) {
					field.setAccessible(true);
					field.set(bean, proxyBean);
					signs.put(beanName, true);
					LOG.info("field inject [{}] -> [{}]", beanName, field);
				}
			}
		}
	}

	/**
	 * bean的setter注入(当发现@Injection注解时)
	 * @deprecated 字段注入更方便
	 */
	public void setterInjection() {
		beanContainer.forEach((key, element) -> {
			Method[] methods = element.getClass().getDeclaredMethods();
			Arrays.asList(methods).parallelStream()
				.filter(method -> method.getName().startsWith("set"))
				.forEach(method -> {
					
				if (!method.isAnnotationPresent(Injection.class)) 
					return;
				
				Class<?>[] params = method.getParameterTypes();
				Object[] injects = new Object[params.length];
				for (int i = 0; i < params.length; i++) {
					
					Object object = findBean(params[i], beanContainer);
					Object proxy = proxyHandler.proxyBean(object);
					if (proxy == object || proxy == null) {
						injects[i] = object;
					}
					else {
						injects[i] = proxy;
					}
				}
				try {
					method.invoke(element, injects);
					LOG.info("setter inject [{}] -> [{}]", key, method);
				} catch (Exception e) {
					LOG.error("set注入异常", e);
					System.exit(0);
				}
			});
		});
	}

	/**
	 * bean的构造方法注入(当发现@Injection注解时)
	 * @throws Exception
	 * @deprecated 字段注入更好, 这种注入方式还不如配置文件
	 */
	public void constructorInjection() throws Exception {
		for (Entry<String, Object> entry : beanContainer.entrySet()) {
			Object element = entry.getValue();
			Constructor<?>[] constructors = element.getClass().getConstructors();
			
			for (Constructor<?> constructor : constructors) {
				if (!constructor.isAnnotationPresent(Injection.class)) 
					continue;
				
				Class<?>[] params = constructor.getParameterTypes();
				Object[] injects = new Object[params.length];
				
				for (int i = 0; i < params.length; i++) {
					
					Object object = findBean(params[i], beanContainer);
					Object proxy = proxyHandler.proxyBean(object);
					if (proxy == object || proxy == null) {
						injects[i] = object;
					}
					else {
						injects[i] = proxy;
					}
				}
				
				beanContainer.put(entry.getKey(), constructor.newInstance(injects));
				LOG.info("constructor inject [{}]", constructor);
			}
		}
	}
	
	/**
	 * 判断bean容器中是否包含指定类型的对象
	 * @param target
	 * @param values
	 * @return
	 */
	private Object findBean(Class<?> target, Map<String, Object> values) {
		Object object = values.get(target.getSimpleName());
		if (object != null) 
			return object;
		
		for (Entry<String, Object> kv : values.entrySet()) {
			if (target.isAssignableFrom(kv.getValue().getClass()))
				return kv.getValue();
		}
		return null;
	}
}
