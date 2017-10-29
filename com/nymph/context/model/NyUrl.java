package com.nymph.context.model;

import com.nymph.context.wrapper.ContextWrapper;
/**
 * Url解析器将会解析的对象
 * @author LiuYang, LiangTianDong
 * @date 2017年10月29日下午9:59:39
 */
public class NyUrl {
	
	private ContextWrapper wrapper;
	
	public NyUrl(ContextWrapper wrapper) {
		this.wrapper = wrapper;
	}

	public ContextWrapper getWrapper() {
		return wrapper;
	}
	
}
