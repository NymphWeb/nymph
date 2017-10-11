package com.nymph.exception;

/**
 * 请求方式不匹配时会抛出此异常(get方式请求post资源?)	
 * @author NYMPH
 * @date 2017年10月7日下午8:25:16
 */
public class PatternNoMatchException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public PatternNoMatchException(String now) {
		super(String.format("请求方式不匹配, 当前请求方式[%s]", now));
	}

}
