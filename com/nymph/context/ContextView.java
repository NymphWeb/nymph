package com.nymph.context;

/**
 * 视图解析器将要解析的对象
 * @author NYMPH
 * @date 2017年9月21日下午8:16:39
 */
public class ContextView {

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

	public ContextView(ContextWrapper context, Object returnResult, String dateFormat, boolean isJSON) {
		this.context = context;
		this.returnResult = returnResult;
		this.dateFormat = dateFormat;
		this.isJSON = isJSON;
	}
}
