package com.nymph.bean.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import com.nymph.utils.BasicUtils;
/**
 * JDK的动态代理, 被代理的对象必须要实现某个接口才能使用
 * @author VULCAN
 * @date 2017年10月4日下午8:04:28
 */
public class JdkAopUtils {

	/**
	 * 获取代理对象, jdk的代理对象只能强转成父类才能正常使用, 所以他必须实现一个接口
	 * @param target	被代理的对象
	 * @param h			代理的具体操作在此对象的invoke方法内进行
	 * @return			jdk的代理对象
	 */
	public static Object getProxy(Object target, InvocationHandler h) {
		Class<?>[] interfaces = target.getClass().getInterfaces();
		return Proxy.newProxyInstance(BasicUtils.getDefaultClassLoad(), interfaces, h);
	}
}
