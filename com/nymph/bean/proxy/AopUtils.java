package com.nymph.bean.proxy;

import com.nymph.utils.BasicUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * 动态代理
 * @author NYMPH
 * @date 2017年10月4日下午7:58:17
 */
public abstract class AopUtils {

	/**
	 * 获取代理对象, jdk的代理对象只能强转成父类才能正常使用, 所以他必须实现一个接口
	 * @param target	被代理的对象
	 * @param h			代理的具体操作在此对象的invoke方法内进行
	 * @return			jdk的代理对象
	 */
	public static Object getProxy(Object target, InvocationHandler h) {
		Class<?>[] interfaces = target.getClass().getInterfaces();
		if (interfaces.length == 0) {
			throw new IllegalArgumentException("目标对象必须实现至少一个接口");
		}

		return Proxy.newProxyInstance(BasicUtil.getDefaultClassLoad(), interfaces, h);
	}

	/**
	 * 获取代理对象, jdk的代理对象只能强转成父类才能正常使用, 所以他必须实现一个接口
	 * @param target	被代理的对象Class
	 * @param h			代理的具体操作在此对象的invoke方法内进行
	 * @return			jdk的代理对象
	 */
	public static Object getProxy(Class<?> target, InvocationHandler h) {
		Class<?>[] interfaces = null;
		if (target.isInterface()) {
			interfaces = new Class<?>[]{target};
		}

		if (interfaces == null) {
			interfaces = target.getInterfaces();
			if (interfaces.length == 0) {
				throw new IllegalArgumentException("target必须为接口或者接口的实现类");
			}
		}

		return Proxy.newProxyInstance(BasicUtil.getDefaultClassLoad(), interfaces, h);
	}
}
