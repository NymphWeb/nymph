package com.nymph.transfer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.nymph.context.ContextWrapper;
/**
 * 对request 和session的进行操作的类
 * @author liuYang
 * @author lianTianDong
 */
public class Transfer {
	/** 异步请求对象*/
	private final ContextWrapper wrapper;
	
	public Transfer(ContextWrapper wrapper) {
		this.wrapper = wrapper;
	}
	/**
	 * 将参数绑定到request中
	 * @param name	参数的名字
	 * @param value	参数的值
	 */
	public void ofRequest(String name, Object value) {
		getRequest().setAttribute(name, value);
	}
	/**
	 * 将参数绑定到session中
	 * @param name	参数的名字
	 * @param value	参数的值
	 */
	public void ofSession(String name, Object value) {
		getSession().setAttribute(name, value);
	}
	/**
	 * 注销session
	 */
	public void invalidSession() {
		getSession().invalidate();
	}
	
	public HttpServletRequest getRequest() {
		return wrapper.httpRequest();
	}
	
	public HttpServletResponse getResponse() {
		return wrapper.httpResponse();
	}
	
	public HttpSession getSession() {
		return wrapper.httpRequest().getSession();
	}
}
