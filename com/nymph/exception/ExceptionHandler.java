package com.nymph.exception;

import com.nymph.context.wrapper.AsyncWrapper;
/**
 * 关于异常处理的接口
 * @author LiuYang
 * @author LiangTianDong
 * @date 2017年10月1日下午11:07:47
 */
public interface ExceptionHandler {
	/**
	 * 当异常出现时需要进行的处理
	 * @param wrapper 异步请求对象
	 * @param throwable 抛出的异常
	 */
	void handle(AsyncWrapper wrapper, Throwable throwable);

}
