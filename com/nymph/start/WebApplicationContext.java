package com.nymph.start;

import com.nymph.bean.component.BeansComponent;
import com.nymph.config.WebConfig;
import com.nymph.context.impl.AbstractResolver;
import com.nymph.exception.EnableExceptionHandler;
import com.nymph.exception.ExceptionHandler;
import com.nymph.exception.impl.ExceptionHandlerImpl;
import com.nymph.utils.BasicUtil;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
	protected static final String CORE_REQUEST_DISPATCHER = "com.nymph.context.impl.NyDispatcher";
	/**
	 *  默认的bean处理器
	 */
	protected static final String DEFAULT_BEANS_HANDLER = "com.nymph.bean.impl.DefaultBeansHandler";
	/**
	 *  关于web方面的配置类
	 */
	protected WebConfig config;
	
	/**
	 * 加载拦截器和其他组件
	 * @throws Exception 
	 */
	protected void loadEqulasConfig() throws Exception {
		// 初始化组件
		initializedComponents();
		// 加载拦截器链
		List<String> interceptorChain = config.getInterceptors();
		intercepts.addAll(BasicUtil.newInstance(interceptorChain));
		Collections.sort(intercepts);
	}
	
	protected void initializedComponents() {
		BeansComponent beansComponent = beansFactory.getBeansComponent();
		Optional<Object> exceptionComponent = beansComponent.getComponent(EnableExceptionHandler.class);
		exceptionHandler = (ExceptionHandler) exceptionComponent.orElseGet(ExceptionHandlerImpl :: new);
		// 拦截器链
		intercepts = beansComponent.getInterceptors();

	}
}