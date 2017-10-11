package com.nymph.bean.impl;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.nymph.bean.BeansComponent;
import com.nymph.bean.BeansFactory;
import com.nymph.bean.BeansHandler;
import com.nymph.bean.BeansProxyHandler;
import com.nymph.bean.EnableBeanProxyHandler;
import com.nymph.bean.HttpBeansContainer;
import com.nymph.config.Configuration;

/**
 * BeansFactory的实现	
 * @author NYMPH
 * @date 2017年10月3日下午2:26:28
 */
public class DefaultBeansFactory implements BeansFactory {
	/**
	 *  存放bean实例的容器, 以类名为键
	 */
	private final Map<String, Object> container = new ConcurrentHashMap<>(512);
	/**
	 *  bean扫描器的实现
	 */
	private BeanScannerForClasspathAndJar autoScanBeans;
	/**
	 *  bean依赖自动注入的实现
	 */
	private BeanPropertyValueInject autoInjectBeans;
	/**
	 *  httpbean的容器
	 */
	private HttpBeansContainer httpContainer;
	/**
	 *  bean组件
	 */
	private BeansComponent beansComponent;
	/**
	 *  bean的基础处理器
	 */
	private BeansHandler beansHandler;
	/**
	 *  bean的动态代理处理器
	 */
	private BeansProxyHandler proxyHandler;
	/**
	 *  配置文件类
	 */
	private Configuration Configuration;
	
	public void initScannerComponent() {
		autoScanBeans = new BeanScannerForClasspathAndJar(
			container, httpContainer, beansHandler, beansComponent);
		autoInjectBeans = new BeanPropertyValueInject(container);
	}
	
	public DefaultBeansFactory() {
		httpContainer = new DefaultHttpBeansContainer();
		beansComponent = new DefaultBeansComponent();
	}
	
	/**
	 * 将bean对象放入容器中, 并且为bean对象的@Injection注解字段和方法注入值
	 */
	@Override
	public void register() {
		initScannerComponent();
		// 扫描指定路径的bean
		autoScanBeans.scanner(Configuration);
		initBeansProxyHandler();
		// 为bean对象的@Injection注解字段和方法注入值
		autoInjectBeans.injection();
	}
	
	public void initBeansProxyHandler() {
		Optional<Object> proxyComponent = 
			beansComponent.getComponent(EnableBeanProxyHandler.class);
		setBeansProxyHandler((BeansProxyHandler) proxyComponent.orElse(null));
		autoInjectBeans.setBeansProxyHandler(proxyHandler);
	}

	@Override
	public Object getBean(String beanName) {
		return container.get(beanName);
	}
	
	@Override
	public Collection<?> getBeans() {
		return container.values();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getBean(Class<T> beanClass) {
		return (T) container.get(beanClass.getSimpleName());
	}

	@Override
	public HttpBeansContainer getHttpBeansContainer() {
		return httpContainer;
	}

	@Override
	public void setHttpBeansContainer(HttpBeansContainer httpHandler) {
		this.httpContainer = httpHandler;
	}

	@Override
	public BeansHandler getBeansHandler() {
		return beansHandler;
	}

	@Override
	public void setBeansHandler(BeansHandler beansHandler) {
		this.beansHandler = beansHandler;
	}

	@Override
	public Configuration getConfiguration() {
		return Configuration;
	}

	@Override
	public void setConfiguration(Configuration Configuration) {
		this.Configuration = Configuration;
	}

	@Override
	public BeansComponent getBeansComponent() {
		return beansComponent;
	}

	@Override
	public void setBeansComponent(BeansComponent beansComponent) {
		this.beansComponent = beansComponent;
	}

	@Override
	public BeansProxyHandler getBeansProxyHandler() {
		return proxyHandler;
	}

	@Override
	public void setBeansProxyHandler(BeansProxyHandler proxyHandler) {
		this.proxyHandler = proxyHandler;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DefaultBeansFactory [container=");
		builder.append(container);
		builder.append("]");
		return builder.toString();
	}
	
}
