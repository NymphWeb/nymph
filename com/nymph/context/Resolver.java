package com.nymph.context;

/**
 * 并发的解析器基类,子类必须实现run方法来达到并发的处理请求
 * @author LiangTianDong
 * @author LiuYang
 * @date 2017年9月26日2017年9月26日
 */
public interface Resolver extends Runnable{
	
	/**
	 * 解析器的解析逻辑的具体的实现
	 * @throws Throwable 解析过程中可能出现的异常
	 */
	void resolver() throws Throwable;
	
}
