package com.nymph.exception;

/**
 * 方法参数注入异常	
 * @author NYMPH
 * @date 2017年10月7日下午7:13:37
 */
public class MethodParamInjectException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public MethodParamInjectException() {
	}

	public MethodParamInjectException(String name) {
		super(String.format("参数绑定失败: mapping: '%s'", name));
	}

}