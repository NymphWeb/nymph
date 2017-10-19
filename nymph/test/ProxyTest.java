package test;

import com.nymph.annotation.ConfigurationBean;
import com.nymph.bean.BeansProxyHandler;
import com.nymph.bean.EnableBeanProxyHandler;
import com.nymph.bean.proxy.AopUtils;

@EnableBeanProxyHandler
public class ProxyTest implements BeansProxyHandler {

	@Override
	public Object proxyBean(Object bean) {
		if (bean.getClass().isAnnotationPresent(ConfigurationBean.class)) {
			return AopUtils.getProxy(bean, (o, m, r, p)->{
				System.out.println("proxy----------");
				return m.invoke(bean, r);
			});
		}
		return bean;
	}

}
