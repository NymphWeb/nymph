package com.nymph.bean.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
/**
 * cglib的动态代理	
 * @author NYMPH
 * @date 2017年10月4日下午7:58:17
 */
public class CglibAopUtils {
	
	@SuppressWarnings("unchecked")
	public static <T> T getProxy(Class<T> target, MethodInterceptor callback) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(target);
		enhancer.setCallback(callback);
		return (T) enhancer.create();
	}
	
	
	@SuppressWarnings("unchecked")
	public static <T> T getProxy(T target, MethodInterceptor callback) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(target.getClass());
		enhancer.setCallback(callback);
		return (T) enhancer.create();
	}
}
