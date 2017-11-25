package com.nymph.context;

/**
 * 参数解析器	
 * @author NYMPH
 * @date 2017年9月26日2017年9月26日
 */
public interface ResovlerParameter extends Resolver {
	
	/**
	 * 将request中获取到的参数注入到HttpBean方法的形参里
	 * @return	    		  注入完成的方法参数
	 * @throws Throwable 	  注入时可能出现的异常, 如类型转换异常等
	 */
	Object[] injectParameters() throws Throwable;
	/**
	 * 执行Http请求所映射的方法
	 * @param args			被invoke方法的参数
	 * @return 				被代理的方法的返回值
	 * @throws Throwable	HttpBean的异常
	 */
	Object invokeMethod(Object[] args) throws Throwable;
}
