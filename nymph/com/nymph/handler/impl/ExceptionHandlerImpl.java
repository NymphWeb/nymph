package com.nymph.handler.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nymph.core.impl.AsyncRequestWrapper;
import com.nymph.exception.MethodReturnVoidException;
import com.nymph.exception.RequestInterceptException;
import com.nymph.exception.PatternNoMatchException;
import com.nymph.exception.NoSuchClassException;
import com.nymph.handler.ExceptionHandler;
import com.nymph.utils.BasicUtils;
/**
 * @comment 异常处理的实现	
 * @author LiuYang
 * @author LiangTianDong
 * @date 2017年10月1日下午11:12:31
 */
public class ExceptionHandlerImpl implements ExceptionHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandlerImpl.class);

	@Override
	public void handle(AsyncRequestWrapper wrapper, Throwable throwable) {
		if (throwable instanceof RequestInterceptException) {
			LOGGER.info("有一个请求被拦截: {}", wrapper.httpRequest().getRequestURL());
			
		} else if (throwable instanceof PatternNoMatchException) {
			LOGGER.warn(throwable.getMessage());
			wrapper.sendError(throwable.getMessage());
			
		} else if (throwable instanceof NoSuchClassException) {
			LOGGER.warn(throwable.getMessage());
			wrapper.send404(throwable.getMessage());
			
		} else if (throwable instanceof MethodReturnVoidException) {
			LOGGER.info("当前方法返回值为void 因此直接提交请求");
			
		} else {
			wrapper.sendError(BasicUtils.join(throwable, throwable.getStackTrace()));
			LOGGER.error(null, throwable);
			throwable.printStackTrace();
		}
	}

}
