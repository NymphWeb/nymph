package com.nymph.start;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.nymph.bean.BeansComponent;
import com.nymph.config.WebConfig;
import com.nymph.core.impl.AbstractResolver;
import com.nymph.handler.EnableExceptionHandler;
import com.nymph.handler.ExceptionHandler;
import com.nymph.handler.impl.ExceptionHandlerImpl;
import com.nymph.utils.BasicUtils;

/**
 * 启动应用上下文并且初始化一些配置
 * @author NYMPH
 * @date 2017年10月5日下午8:02:58
 */
public abstract class WebApplicationContext extends AbstractResolver {
	/**
	 *  字符编码过滤器
	 */
	protected static final String DEFAULT_ENCODING_FILTER = "com.nymph.filter.EncodingFilter";
	/**
	 *  核心调度器
	 */
	protected static final String CORE_REQUEST_DISPATCHER = "com.nymph.core.impl.NyDispatcher";
	/**
	 *  默认的bean处理器
	 */
	protected static final String DEFAULT_BEANS_HANDLER = "com.nymph.bean.impl.DefaultBeansHandler";
	/**
	 *  关于web方面的配置类
	 */
	protected WebConfig config;
	
	/**
	 * 加载 内嵌 和 webxml 都必须的配置
	 * @throws Exception 
	 */
	protected void loadEqulasConfig() throws Exception {
		// 初始化组件
		initializedComponents();
		// 加载拦截器链
		List<String> interceptorChain = config.getInterceptors();
		intercepts= BasicUtils.newInstance(interceptorChain);
		Collections.sort(intercepts);
	}
	
	protected void initializedComponents() {
		BeansComponent beansComponent = beansFactory.getBeansComponent();
		Optional<Object> exceptionComponent = beansComponent.getComponent(EnableExceptionHandler.class);
		exceptionHandler = (ExceptionHandler) exceptionComponent.orElseGet(ExceptionHandlerImpl :: new);
	}
}