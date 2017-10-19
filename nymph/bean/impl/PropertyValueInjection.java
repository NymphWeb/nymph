package com.nymph.bean.impl;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nymph.annotation.Injection;
import com.nymph.bean.BeansProxyHandler;

/**
 * 注入bean中被@Injection注解标识的字段或方法	
 * @author LiuYang
 * @author LiangTianDong
 * @date 2017年10月3日下午2:37:44
 */
public class PropertyValueInjection {
	
	private static final Logger LOG = LoggerFactory.getLogger(PropertyValueInjection.class);
	
	private Map<String, Object> beanContainer;
	
	private Map<String, Boolean> signs;
	
	private BeansProxyHandler proxyHandler;

	public void injection() {
		try {
			fieldInjection();
			// TODO 依赖注入应该使用字段注入的方式, 多种方式在有动态代理时不好区分
			// TODO 因为代理之后原先的bean的字段都获取不到了, 无法进行注入操作
			//setterInjection();
			//constructorInjection();
		} catch (Exception e) {
			LOG.error("Injection Exception 注入异常", e);
		}
	}
	
	public PropertyValueInjection(Map<String, Object> beanContainer) {
		this.beanContainer = beanContainer;
		this.signs = new HashMap<>(beanContainer.size());
	}
	
	public void setBeansProxyHandler(BeansProxyHandler proxyHandler) {
		this.proxyHandler = proxyHandler;
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

				if (proxyHandler != null) {
					Object proxyBean = proxyHandler.proxyBean(inject);
					String proxyName = inject.getClass().getName();
					if ((proxyBean != inject) && (signs.get(proxyName) == null)) {
						proxyHandler(inject, proxyName);
					}
				}

				if (null == signs.get(beanName)) {
					field.setAccessible(true);
					field.set(bean, inject);
					LOG.info("field inject [{}] -> [{}]", field.getName(), beanName);
				}
			}
			signs.put(beanName, true);
		}
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
					LOG.info("field inject [{}] -> [{}]",field.getName() , name);
			}
		}
		signs.put(name, true);
	}
	
	/**
	 * 判断bean容器中是否包含指定类型的对象
	 * @param target
	 * @param values
	 * @return
	 */
	private Object findBean(Class<?> target, Map<String, Object> values) {
		Object object = values.get(target.getName());
		if (object != null)
			return object;

		for (Entry<String, Object> kv : values.entrySet()) {
			if (target.isAssignableFrom(kv.getValue().getClass()))
				return kv.getValue();
		}
		return null;
	}

//	/**
//	 * bean的setter注入(当发现@Injection注解时)
//	 */
//	public void setterInjection() {
//		beanContainer.forEach((key, element) -> {
//			Method[] methods = element.getClass().getDeclaredMethods();
//			Arrays.asList(methods).parallelStream()
//				.filter(method -> method.getName().startsWith("set"))
//				.forEach(method -> {
//
//				if (!method.isAnnotationPresent(Injection.class))
//					return;
//
//				Class<?>[] params = method.getParameterTypes();
//				Object[] injects = new Object[params.length];
//				for (int i = 0; i < params.length; i++) {
//
//					Object object = findBean(params[i], beanContainer);
//					Object proxy = isProxy ? null : proxyHandler.proxyBean(object);
//					if (proxy != object) {
//						injects[i] = object;
//					}
//					else {
//						injects[i] = proxy;
//					}
//				}
//				try {
//					method.invoke(element, injects);
//					LOG.info("setter inject [{}] -> [{}]", key, method);
//				} catch (Exception e) {
//					LOG.error("set注入异常", e);
//					System.exit(0);
//				}
//			});
//		});
//	}
//
//	/**
//	 * bean的构造方法注入(当发现@Injection注解时)
//	 * @throws Exception
//	 */
//	public void constructorInjection() throws Exception {
//		for (Entry<String, Object> entry : beanContainer.entrySet()) {
//			Object element = entry.getValue();
//			Constructor<?>[] constructors = element.getClass().getConstructors();
//
//			for (Constructor<?> constructor : constructors) {
//				if (!constructor.isAnnotationPresent(Injection.class))
//					continue;
//
//				Class<?>[] params = constructor.getParameterTypes();
//				Object[] injects = new Object[params.length];
//
//				for (int i = 0; i < params.length; i++) {
//
//					Object object = findBean(params[i], beanContainer);
//					Object proxy = isProxy ? null : proxyHandler.proxyBean(object);
//					if (proxy != object) {
//						injects[i] = object;
//					}
//					else {
//						injects[i] = proxy;
//					}
//				}
//
//				beanContainer.put(entry.getKey(), constructor.newInstance(injects));
//				LOG.info("constructor inject [{}]", constructor);
//			}
//		}
//	}
}
