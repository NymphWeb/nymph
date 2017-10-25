package com.nymph.exception;

/**
 * 当找不到url映射的类时会抛出此异常
 * @author NYMPH
 * @date 2017年10月7日下午7:15:06
 */
public class NoSuchClassException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NoSuchClassException(String url) {
		super(String.format("无法解析url: %s , cause by: 找不到映射的类", url));
	}
}
