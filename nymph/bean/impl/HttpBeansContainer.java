/**
 * 
 */
package com.nymph.bean.impl;

import com.nymph.annotation.HTTP;
import com.nymph.annotation.Request;
import com.nymph.utils.BasicUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 关于web层的bean的一些处理, 主要是将一个完整的url映射到具体的一个方法, 这样可以加快访问的速度, 直接通过浏览器
 *     的url地址的字符串就能获取到对应的类和方法
 * @author NYMPH
 * @date 2017年9月26日2017年9月26日
 */
public class HttpBeansContainer {

	private final Map<String, HttpBean> httpMap = new HashMap<>(256);

	/**
	 * 对拥有@HTTP注解的bean进行处理主要是将@HTTP注解的value和@Request系列注解(@GET这种)
	 *     的value拼接成一个完整的表示url地址的字符串, 然后存入map中, 这样当一个请求访问过来
	 *     时只需要拿到这个请求的uri就可以直接获取到对应的Class和Method,如果用@UrlVal
	 *     注解这种占位符形式的url的话就只有遍历来寻找是否存在请求所映射的类了
	 * @param clazz	待处理的Class
	 */
	public void filterAlsoSave(Class<?> clazz) throws IllegalAccessException {
		HTTP http; // 没有@HTTP注解的bean则不进行处理
		if ((http = clazz.getAnnotation(HTTP.class)) == null)
			return;

		for (Method method : clazz.getDeclaredMethods()) {
			Annotation[] annotations = method.getAnnotations();
			Annotation request = BasicUtils.getAnno(annotations, Request.class);
			if (request != null) {
				methodModifierCheck(method);
				String url = joinUrl(http.value(), request);
				httpMap.put(url, new HttpBean(clazz.getName(), method));
			}
		}
	}
	/**
	 * 将web映射对象的@HTTP注解和 方法的请求注解(@GET,@POST 等) 的值拼接成一个完整的url
	 * @param spaceVal	-@HTTP注解value方法的值
	 * @param method	方法上的请求方式注解(@GET或其他)
	 * @return			拼接好的url
	 */
	private String joinUrl(String spaceVal, Annotation method) {
		String methodVal = annoValue(method, "value");
		return ("/".equals(spaceVal) || spaceVal.equals("") ? "" :
				spaceVal.startsWith("/") ? spaceVal : "/" + spaceVal)
				+ (!methodVal.startsWith("/") ? "/" + methodVal : methodVal);
	}
	/**
	 * 获取方法上@GET ,@POST 等请求类型注解的值
	 * @param request
	 * @param methodName
	 * @return
	 */
	private String annoValue(Annotation request, String methodName) {
		try {
			return String.valueOf(request.getClass().getMethod(methodName).invoke(request));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 方法修饰符检查, 不为public时抛出异常
	 * @param method
	 * @throws IllegalAccessException
	 */
	private void methodModifierCheck(Method method) throws IllegalAccessException {
		if (method.getModifiers() != Modifier.PUBLIC) {
			throw new IllegalAccessException("方法的修饰符必须为public , method: " + method.getName());
		}
	}

	@Override
	public String toString() {
		return String.valueOf(httpMap);
	}

	public HttpBean getHttpBean(String url) {
		return httpMap.get(url);
	}

	public Set<Entry<String, HttpBean>> getAllHttpBean() {
		return httpMap.entrySet();
	}

	/** httpBean容器内存放的对象 */
	public class HttpBean implements Cloneable {
		private String name; // web层映射类的类名

		private Method method; // 路径对应的方法

		// @UrlVal注解的value值
		private Map<String, String> placeHolder;

		public HttpBean(String name, Method method) {
			this.name = name;
			this.method = method;
		}

		public HttpBean() {}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Method getMethod() {
			return method;
		}

		public void setMethod(Method method) {
			this.method = method;
		}

		public Map<String, String> getPlaceHolder() {
			return placeHolder;
		}

		public void setPlaceHolder(Map<String, String> placeHolder) {
			this.placeHolder = placeHolder;
		}

		public HttpBean initialize(Map<String, String> placeHolder) throws CloneNotSupportedException {
			HttpBean httpBean = (HttpBean)this.clone();
			httpBean.setPlaceHolder(placeHolder);
			return httpBean;
		}
	}
}
