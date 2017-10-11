package test;

import com.nymph.annotation.ConfBean;
import com.nymph.bean.BeansProxyHandler;
import com.nymph.bean.EnableBeanProxyHandler;
import com.nymph.bean.proxy.CglibAopUtils;

@EnableBeanProxyHandler
public class ProxyTest implements BeansProxyHandler {

	@Override
	public Object proxyBean(Object bean) {
		if (bean.getClass().isAnnotationPresent(ConfBean.class)) {
			System.out.println(bean);
			return CglibAopUtils.getProxy(bean, (o, m, r, p)->{
				System.out.println("proxy----------");
				return m.invoke(bean, r);
			});
		}
		return bean;
	}

}
