package com.nymph.bean;

public interface BeansProxyHandler {
	/**
	 * Jdk的动态代理, 返回null时表示不代理
	 * @param bean	bean的实例
	 * @return		代理后的对象, 为null时不会存入容器
	 */
	Object proxyBean(Object bean);

}
