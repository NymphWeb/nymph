package com.nymph.context;

import com.nymph.bean.BeansComponent;
import com.nymph.bean.web.WebApplicationBeansFactory;
import com.nymph.config.Configuration;
import com.nymph.config.WebConfig;
import com.nymph.exception.handle.EnableExceptionHandler;
import com.nymph.exception.handle.ExceptionHandler;
import com.nymph.exception.impl.DefaultExceptionHandler;
import com.nymph.interceptor.Interceptors;
import com.nymph.utils.BasicUtil;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 初始化应用上下文的一些配置
 * @author NYMPH
 * @date 2017年10月5日下午8:02:58
 */
public abstract class WebApplicationInitialization {
	/**
	 * 字符编码过滤器
	 */
	protected static final String DEFAULT_ENCODING_FILTER = "com.nymph.filter.EncodingFilter";
	/**
	 * 核心调度器
	 */
	protected static final String CORE_REQUEST_DISPATCHER = "com.nymph.context.AsyncDispatcher";
	/**
	 * 默认的bean处理器
	 */
	protected static final String DEFAULT_BEANS_HANDLER = "com.nymph.bean.core.DefaultBeansHandler";
	/**
	 * 关于web方面的配置类
	 */
	protected WebConfig config;
	
	/**
	 * 加载拦截器和其他组件
	 * @throws Exception 
	 */
	protected void initBaseConfig() throws Exception {
		// 初始化组件
		initializedComponents();
		// 加载拦截器链
		List<String> interceptorChain = config.getInterceptors();
		getIntercepts().addAll(BasicUtil.newInstance(interceptorChain));
		Collections.sort(getIntercepts());
	}

	/**
	 * 初始化一些组件(拦截器链, 异常处理器等)
	 */
	protected void initializedComponents() {
		BeansComponent beansComponent = getBeansFactory().getBeansComponent();
		Optional<Object> exceptionComponent = beansComponent.getComponent(EnableExceptionHandler.class);
		setExceptionHandler((ExceptionHandler) exceptionComponent.orElseGet(DefaultExceptionHandler :: new));
		setIntercepts(beansComponent.getInterceptors());

	}

	protected void setIntercepts(List<Interceptors> intercepts) {
		AbstractResolver.intercepts = intercepts;
	}

	protected List<Interceptors> getIntercepts() {
		return AbstractResolver.intercepts;
	}

	protected void setExceptionHandler(ExceptionHandler exceptionHandler) {
		AbstractResolver.exceptionHandler = exceptionHandler;
	}

	protected ExceptionHandler getExceptionHandler() {
		return AbstractResolver.exceptionHandler;
	}

	protected void setConfiguration(Configuration configuration) {
		AbstractResolver.configuration = configuration;
	}

	protected Configuration getConfiguration() {
		return AbstractResolver.configuration;
	}

	protected WebApplicationBeansFactory getBeansFactory() {
		return AbstractResolver.beansFactory;
	}

}