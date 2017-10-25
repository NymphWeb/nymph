package com.nymph.context;

import java.lang.reflect.Method;

import com.nymph.context.wrapper.MethodWrapper;

/**
 * 参数解析器	
 * @author NYMPH
 * @date 2017年9月26日2017年9月26日
 */
public interface ParamResolver extends Resolver {
	
	/**
	 * 注入url映射的类的具体方法的所有形参
	 * @param methodWrapper  Method包装类
	 * @return	    		  注入完成的方法参数
	 * @throws Throwable 	  注入时可能出现的异常, 如类型转换异常等
	 */
	Object[] methodParamsInjection(MethodWrapper methodWrapper) throws Throwable;
	/**
	 * 此方法实现了拦截器。在执行目标方法之前执行前置拦截器链和后置拦截器链
	 * @param target 		被代理的目标对象
	 * @param method 		目标对象正在执行的方法
	 * @param param 		目标对象方法的参数
	 * @return 				被代理的方法的返回值
	 * @throws Throwable	HttpBean的异常
	 */
	Object intercept(Object target, Method method, Object[] param) throws Throwable;
}
