package com.nymph.bean.component;

/**
 * 处理想要被代理的bean
 * @author LiuYang
 * @author LiangTianDong
 */
public interface BeansProxyHandler {
	/**
	 * Jdk的动态代理, 可以在此返回代理对象注册到bean工厂
	 * @param bean	bean的实例
	 * @return		代理后的对象
	 */
	Object proxyBean(Object bean);

}
