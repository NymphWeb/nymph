package com.nymph.context.core;
/**
 * 视图解析器	
 * @author LiuYang
 * @author LiangTianDong
 * @date 2017年9月26日2017年9月26日
 */
public interface ResolverView extends Resolver{
	/**
	 * 判断结果是转发到请求还是重定向 或者是直接响应json
	 * @param result		http映射bean的方法返回值, 一般为字符串, 返回json时可能是个普通对
	 * 						象或者集合
	 * 
	 * @return				处理完后的字符串, 应为最终不管是json还是跳转页面都会是一个字符串
	 * @throws Exception	可能出现的任何异常
	 */
	String dispatchTypeHandle(Object result) throws Exception;

}
