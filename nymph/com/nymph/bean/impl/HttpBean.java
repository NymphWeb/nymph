package com.nymph.bean.impl;

import java.lang.reflect.Method;
import java.util.Map;
/**
 * 用于保存url地址的映射信息	
 * @author NYMPH
 * @date 2017年10月3日下午4:49:10
 */
public class HttpBean implements Cloneable {
	private String name; // web层映射类的类名

	private Method method; // 路径对应的方法
	
	// @PlaceHolder占位符对应的值
	private Map<String, String> placeHolder;
	
	public HttpBean(String name, Method method) {
		this.name = name;
		this.method = method;
	}
	
	public HttpBean() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public Map<String, String> getPlaceHolder() {
		return placeHolder;
	}

	public void setPlaceHolder(Map<String, String> placeHolder) {
		this.placeHolder = placeHolder;
	}
	
	public HttpBean initialize(Map<String, String> placeHolder) throws CloneNotSupportedException {
		HttpBean httpBean = (HttpBean)this.clone();
		httpBean.setPlaceHolder(placeHolder);
		return httpBean;
	}
}
