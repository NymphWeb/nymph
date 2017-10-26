package com.test;

import com.nymph.annotation.ConfigurationBean;
import com.nymph.bean.component.BeansProxyHandler;
import com.nymph.bean.component.EnableBeanProxyHandler;
import com.nymph.bean.proxy.AopUtils;

@EnableBeanProxyHandler
public class TestProxy implements BeansProxyHandler {

	@Override
	public Object proxyBean(Object bean) {
		if (bean.getClass().isAnnotationPresent(ConfigurationBean.class)) {
			return AopUtils.getProxy(bean, (o, m, r, p)->{
				return m.invoke(bean, r);
			});
		}
		return bean;
	}

}
