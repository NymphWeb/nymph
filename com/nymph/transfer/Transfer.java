package com.nymph.transfer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.nymph.context.wrapper.ContextWrapper;
/**
 * 对request 和session的进行操作的类
 * @author liuYang
 * @author lianTianDong
 */
public class Transfer {
	/** 异步请求对象*/
	private final ContextWrapper async;
	
	public Transfer(ContextWrapper async) {
		this.async = async;
	}
	/**
	 * 将参数绑定到request中
	 * @param name	参数的名字
	 * @param value	参数的值
	 */
	public void forRequest(String name, Object value) {
		async.httpRequest().setAttribute(name, value);
	}
	/**
	 * 将参数绑定到session中
	 * @param name	参数的名字
	 * @param value	参数的值
	 */
	public void forSession(String name, Object value) {
		HttpServletRequest request = async.httpRequest();
		request.getSession().setAttribute(name, value);
	}
	/**
	 * 注销session
	 */
	public void invalidSession() {
		HttpServletRequest request = async.httpRequest();
		request.getSession().invalidate();
	}
	
	public HttpServletRequest getRequest() {
		return async.httpRequest();
	}
	
	public HttpServletResponse getResponse() {
		return async.httpResponse();
	}
	
	public HttpSession getSession() {
		return async.httpRequest().getSession();
	}
}
