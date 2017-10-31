package com.nymph.exception.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nymph.context.ContextWrapper;
import com.nymph.exception.NoSuchClassException;
import com.nymph.exception.PatternNoMatchException;
import com.nymph.exception.handle.ExceptionHandler;
import com.nymph.utils.PageCSS;

/**
 * 异常处理的实现
 * @author LiuYang
 * @author LiangTianDong
 * @date 2017年10月1日下午11:12:31
 */
public class DefaultExceptionHandler implements ExceptionHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultExceptionHandler.class);

	@Override
	public void handle(ContextWrapper wrapper, Throwable throwable) {
		if (throwable instanceof PatternNoMatchException) {
			LOGGER.warn(throwable.getMessage(), throwable);
			wrapper.sendError(throwable.getMessage());
			
		} else if (throwable instanceof NoSuchClassException) {
			LOGGER.warn(throwable.getMessage(), throwable);
			wrapper.send404(PageCSS.join(throwable));
			
		} else {
			wrapper.sendError(PageCSS.join(throwable));
			LOGGER.error(null, throwable);
			throwable.printStackTrace();
		}
	}

}
