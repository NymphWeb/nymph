package com.nymph.bean.web;

import com.nymph.bean.*;
import com.nymph.bean.component.DefaultBeansComponent;
import com.nymph.bean.component.EnableBeanProxyHandler;
import com.nymph.bean.component.EnableBeanRegister;
import com.nymph.bean.core.PropertyValueInjection;
import com.nymph.bean.core.ScannerClasspathAndJar;
import com.nymph.config.Configuration;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * BeansFactory的实现	
 * @author LiuYang
 * @author LiangTianDong
 * @date 2017年10月3日下午2:26:28
 */
public class DefaultWebApplicationBeansFactory implements WebApplicationBeansFactory {
	/**
	 *  存放bean实例的容器, 以类名为键
	 */
	private final Map<String, Object> container = new ConcurrentHashMap<>(512);
	/**
	 *  bean扫描器的实现
	 */
	private ScannerClasspathAndJar autoScanBeans;
	/**
	 *  bean依赖注入的实现
	 */
	private PropertyValueInjection autoInjectBeans;
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
	private BeansProxy proxyHandler;
	/**
	 *  配置文件类
	 */
	private Configuration Configuration;

	public DefaultWebApplicationBeansFactory() {
		httpContainer = new HttpBeansContainer();
		beansComponent = new DefaultBeansComponent();
	}
	
	private void initScannerSupport() {
		autoScanBeans = new ScannerClasspathAndJar(
			container, httpContainer, beansHandler, beansComponent);
		autoInjectBeans = new PropertyValueInjection(container);
	}

	private void initBeansExpandComponents() {
		// bean的动态代理处理器
		Optional<Object> proxy = beansComponent.getComponent(EnableBeanProxyHandler.class);
		setBeansProxyHandler((BeansProxy) proxy.orElse(null));
		autoInjectBeans.setBeansProxyHandler(proxyHandler);

		Optional<Object> register = beansComponent.getComponent(EnableBeanRegister.class);
		BeansRegister beansRegister = (BeansRegister) register.orElse(null);
		if (beansRegister != null) {
			container.putAll(beansRegister.registerBeans());
		}
	}

	/**
	 * 将bean对象放入容器中, 并且为bean对象的@Injection注解字段和方法注入值
	 */
	@Override
	public void register() {
		initScannerSupport();
		// 扫描指定路径的bean
		autoScanBeans.scanner(Configuration);
		// 初始化bean组件
		initBeansExpandComponents();
		// 为bean对象的@Injection注解字段和方法注入值
		autoInjectBeans.injection();
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
		return (T) container.get(beanClass.getName());
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
	public BeansProxy getBeansProxyHandler() {
		return proxyHandler;
	}

	@Override
	public void setBeansProxyHandler(BeansProxy proxyHandler) {
		this.proxyHandler = proxyHandler;
	}
	
}
