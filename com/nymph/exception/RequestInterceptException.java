package com.nymph.exception;

/**
 * 请求被拦截时会抛出此异常来终止程序继续运行
 * @author NYMPH
 * @date 2017年10月7日下午8:26:22
 */
public class RequestInterceptException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public RequestInterceptException() {}
	
	public RequestInterceptException(String message) {
		super(message);
	}

}
