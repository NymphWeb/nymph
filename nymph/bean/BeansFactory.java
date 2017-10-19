package com.nymph.bean;

import java.util.Collection;

import com.nymph.bean.impl.HttpBeansContainer;
import com.nymph.config.Configuration;

/**
 * bean工厂接口
 * @author NYMPH
 * @date 2017年10月3日下午2:18:41
 */
public interface BeansFactory {
	/**
	 * 将所有的bean注册到容器中
	 */
	void register();

	/**
	 * 注册一个bean到工厂
	 * @param name	bean的名称
	 * @param bean	bean对象
	 */
	void registerBean(String name, Object bean);
	/**
	 * 根据bean的名字获取到bean的实例
	 * @param beanName	bean的名字, 一般为bean的类名
	 * @return			这个bean的实例
	 */
	Object getBean(String beanName);
	/**
	 * 根据bean的类型来获取到bean的实例
	 * @param beanClass bean的类型
	 * @return			bean的实例
	 */
	<T> T getBean(Class<T> beanClass);
	/**
	 * 获取bean工厂的所有bean实例
	 * @return bean实例的集合
	 */
	Collection<?> getBeans();
	/**
	 * 获取HttpBean处理器
	 * @return
	 */
	HttpBeansContainer getHttpBeansContainer();
	/**
	 * plain setter
	 * @param httpHandler HttpBean处理器
	 */
	void setHttpBeansContainer(HttpBeansContainer httpHandler);
	/**
	 * 获取基础的Bean处理器
	 * @return
	 */
	BeansHandler getBeansHandler();
	/**
	 * plain setter
	 * @param beansHandler
	 */
	void setBeansHandler(BeansHandler beansHandler);
	/**
	 * 获取配置对象
	 * @return
	 */
	Configuration getConfiguration();
	/**
	 * 设置配置对象
	 */
	void setConfiguration(Configuration Configuration);
	/**
	 * 获取bean组件对象
	 * @return
	 */
	BeansComponent getBeansComponent();
	/**
	 * 修改bean组件对象
	 * @param beansComponent
	 */
	void setBeansComponent(BeansComponent beansComponent);
	/**
	 * 获取bean的代理处理器
	 * @return
	 */
	BeansProxyHandler getBeansProxyHandler();
	/**
	 * 修改bean的代理处理器
	 * @param proxyHandler
	 */
	void setBeansProxyHandler(BeansProxyHandler proxyHandler);
}
