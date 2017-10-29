package com.nymph.context.model;

import com.nymph.context.wrapper.ContextWrapper;

/**
 * @说明 视图解析器将要解析的对象
 * @作者 NYMPH
 * @创建时间 2017年9月21日下午8:16:39
 */
public class NyView {

	private ContextWrapper context;
	
	private String dateFormat;

	private Object returnResult;
	
	private boolean isJSON;

	public ContextWrapper getContext() {
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

	public NyView(ContextWrapper context, Object returnResult, String dateFormat, boolean isJSON) {
		this.context = context;
		this.returnResult = returnResult;
		this.dateFormat = dateFormat;
		this.isJSON = isJSON;
	}
}
