package com.nymph.context.impl;

import com.nymph.context.wrapper.AsyncWrapper;
import com.nymph.exception.RequestInterceptException;
import com.nymph.interceptor.NyInterceptors;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 主要用于代理@HTTP注解的bean 	
 * @author LiuYang
 * @author LiangTianDong
 * @date 2017年10月4日下午12:59:17
 */
public final class WebProxy {

	private List<NyInterceptors> interceptors;

	/**
	 * 代理目标对象的方法 , 在执行目标方法之前执行前置拦截器链和后置拦截器链
	 * @param target 	被代理的目标对象
	 * @param method 	目标对象正在执行的方法
	 * @param param 	目标对象方法的参数
	 * @param wrapper 	异步请求的wrapper对象 
	 * @return 被代理的方法的返回值
	 * @throws Throwable	HttpBean的异常
	 */
	public Object execute(Object target, Method method, Object[] param, AsyncWrapper wrapper) throws Throwable {
		
		// 拦截器链前置执行的方法
		for (NyInterceptors preHandle : interceptors) {
			if (!preHandle.preHandle(wrapper)) {
				// 如果方法被拦截了将抛出此异常,停止运行下面代码
				throw new RequestInterceptException();
			}
		}
		try {
			Object invoke = method.invoke(target, param);

			// 拦截器链的后置执行方法
			for (NyInterceptors beHandle : interceptors) {
				beHandle.behindHandle(wrapper);
			}
			return invoke;
		} catch (Throwable e) {
			// catch web层的异常
			Throwable cause = e.getCause();
			throw cause == null ? e : cause;
		}
	}

	public WebProxy(List<NyInterceptors> interceptors) {
		this.interceptors = interceptors;
	}
}
