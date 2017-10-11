package com.nymph.core.impl;

import java.util.List;

import com.nymph.bean.BeansFactory;
import com.nymph.config.Configuration;
import com.nymph.core.Resolver;
import com.nymph.handler.ExceptionHandler;
import com.nymph.interceptor.NyInterceptors;

/**
 * 解析器的抽象类, 实现了Runnable接口的run方法
 * @author NYMPH
 * @date 2017年10月3日下午4:36:37
 */
public abstract class AbstractResolver implements Resolver {
	/**
	 *  bean工厂
	 */
	protected static BeansFactory beansFactory;
	/**
	 *  拦截器链, 可以通过实现NyInterceptors接口来拦截请求
	 */
	protected static List<NyInterceptors> intercepts;
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
	protected AsyncRequestWrapper wrapper;
	
	public AbstractResolver(AsyncRequestWrapper asyncRequestWrapper) {
		this.wrapper = asyncRequestWrapper;
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
