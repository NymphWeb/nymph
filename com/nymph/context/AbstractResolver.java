package com.nymph.context;

import java.util.List;

import com.nymph.bean.web.DefaultWebApplicationBeansFactory;
import com.nymph.bean.web.WebApplicationBeansFactory;
import com.nymph.config.Configuration;
import com.nymph.context.core.Resolver;
import com.nymph.exception.handle.ExceptionHandler;
import com.nymph.interceptor.Interceptors;

/**
 * 解析器的抽象类
 * @author LiuYang
 * @author LiangTianDong
 * @date 2017年10月3日下午4:36:37
 */
public abstract class AbstractResolver implements Resolver {
	/**
	 *  bean工厂
	 */
	protected static WebApplicationBeansFactory beansFactory = new DefaultWebApplicationBeansFactory();
	/**
	 *  拦截器链, 可以通过实现Interceptors接口来拦截请求
	 */
	protected static List<Interceptors> intercepts;
	/**
	 *  配置类
	 */
	protected static Configuration configuration;
	/**
	 *  异常处理器
	 */
	protected static ExceptionHandler exceptionHandler;
	/**
	 *  异步请求对象
	 */
	protected ContextWrapper wrapper;
	
	public AbstractResolver(ContextWrapper wrapper) {
		this.wrapper = wrapper;
	}

	public AbstractResolver() {}
	
	@Override
	public void run() {
		try {
			resolver();
		} catch (Throwable throwable) { // 处理 3个解析器的异常
			try {
				exceptionHandler.handle(wrapper, throwable);
			} finally {
				wrapper.commit();
			}
		}
	}
	
	@Override
	public void resolver() throws Throwable {}
}
