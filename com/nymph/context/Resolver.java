package com.nymph.context;

/**
 * 并发的解析器基类,子类必须实现run方法来达到并发的处理请求
 * @author NYMPH
 * @date 2017年9月26日2017年9月26日
 */
public interface Resolver extends Runnable{
	
	void resolver() throws Throwable;
	
}
