/**
 * 
 */
package com.nymph.bean.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.nymph.annotation.HTTP;
import com.nymph.annotation.Request;
import com.nymph.bean.HttpBeansContainer;
import com.nymph.utils.BasicUtils;

/**
 * 关于web层的bean的一些处理, 主要是将一个完整的url映射到具体的一个方法, 这样可以加快访问的速度, 直接通过浏览器
 *     的url地址的字符串就能获取到对应的类和方法
 * @author NYMPH
 * @date 2017年9月26日2017年9月26日
 */
public class DefaultHttpBeansContainer implements HttpBeansContainer{

	private final Map<String, HttpBean> httpMap = new HashMap<>(256);
	
	@Override
	public void filterAlsoSave(Class<?> clazz) {
		try {
			HTTP http = null; // 没有@HTTP注解的bean则不进行处理
			if ((http = clazz.getAnnotation(HTTP.class)) == null)
				return;
			
			for (Method method : clazz.getDeclaredMethods()) {
				Annotation[] annotations = method.getAnnotations();
				Annotation request = BasicUtils.getAnno(annotations, Request.class);
				if (request != null) {
					reflectSetUpCheck(method.getParameters());
					methodModifierCheck(method);

					String url = joinUrl(http.value(), request);
					httpMap.put(url, new HttpBean(clazz.getName(), method));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
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

	/**
	 * eclipse没勾选那个选项反射会获取不到方法参数的名字
	 * @param parameters
	 */
	private void reflectSetUpCheck(Parameter[] parameters) {
		String name = parameters.length > 0 ? parameters[0].getName() : "";
		if (name.matches("^arg\\d+$")) {
			throw new IllegalArgumentException("有一个设置需要选择, eclipse工具请在window->java->compiler "
					+ "勾选最后一行 -> store information about method parameters");
		}
	}
	
	@Override
	public String toString() {
		return String.valueOf(httpMap);
	}
	@Override
	public HttpBean getHttpBean(String url) {
		return httpMap.get(url);
	}
	@Override
	public Set<Entry<String, HttpBean>> getAllHttpBean() {
		return httpMap.entrySet();
	}
}
