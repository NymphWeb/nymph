package com.nymph.bean.proxy;

public abstract class ProxyUtils {

	private static final String CGLIB_PROXY_NAME = "$$";
	
	private static final String JDK_PROXY_NAME = "$Proxy";
	
	public static boolean isCglibProxy(Class<?> clazz) {
		return clazz.getSimpleName().contains(CGLIB_PROXY_NAME);
	}
	
	public static boolean isJdkProxy(Class<?> clazz) {
		return clazz.getSimpleName().contains(JDK_PROXY_NAME);
	}
}
