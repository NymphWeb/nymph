package com.nymph.bean.web;

import com.nymph.bean.BeansComponent;
import com.nymph.bean.BeansFactory;
import com.nymph.bean.BeansHandler;
import com.nymph.bean.BeansProxy;
import com.nymph.config.Configuration;

/**
 * web应用必须的Bean工厂
 * @author LiuYang
 * @author LiangTianDong
 * @date 2017年10月23日上午11:32:44
 */
public interface WebApplicationBeansFactory extends BeansFactory {
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
	BeansProxy getBeansProxyHandler();
	/**
	 * 修改bean的代理处理器
	 * @param proxyHandler
	 */
	void setBeansProxyHandler(BeansProxy proxyHandler);

}
