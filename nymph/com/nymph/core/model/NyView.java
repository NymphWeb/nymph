package com.nymph.core.model;

import com.nymph.core.impl.AsyncRequestWrapper;

/**
 * @说明 视图解析器将要解析的对象
 * @作者 NYMPH
 * @创建时间 2017年9月21日下午8:16:39
 */
public class NyView {

	private AsyncRequestWrapper context;
	
	private String dateFormat;

	private Object returnResult;
	
	private boolean isJSON;

	public AsyncRequestWrapper getContext() {
		return context;
	}
	
	public String getDateFormat() {
		return dateFormat;
	}
	
	public boolean getIsJSON() {
		return isJSON;
	}
	
	public Object getReturnResult() {
		return returnResult;
	}

	public NyView(AsyncRequestWrapper context, Object returnResult, String dateFormat, boolean isJSON) {
		this.context = context;
		this.returnResult = returnResult;
		this.dateFormat = dateFormat;
		this.isJSON = isJSON;
	}
}
