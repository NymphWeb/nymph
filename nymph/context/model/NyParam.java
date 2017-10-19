package com.nymph.context.model;

import java.util.Map;

import com.nymph.bean.impl.HttpBeansContainer;
import com.nymph.context.wrapper.AsyncWrapper;
import com.nymph.transfer.Multipart;
/**
 * @说明 参数解析器将要解析的对象
 * @作者 NYMPH
 * @创建时间 2017年9月21日下午8:16:00
 */
public class NyParam {

	private Map<String, String[]> params;
	
	private AsyncWrapper context;
	
	private Multipart multipart;
	
	private HttpBeansContainer.HttpBean httpBean;
	
	public NyParam(	HttpBeansContainer.HttpBean httpBean,
					Map<String, String[]> params,
					AsyncWrapper context,
					Multipart multipart) {
		
		
		this.httpBean = httpBean;
		this.context = context;
		this.params = params;
		this.multipart = multipart;
	}
	
	public HttpBeansContainer.HttpBean getHttpBean() {
		return httpBean;
	}

	public String getPlaceHolder(String url) {
		return httpBean.getPlaceHolder().get(url);
	}

	public AsyncWrapper getContext() {
		return context;
	}

	public Map<String, String[]> getParams() {
		return params;
	}

	public Multipart getMultipart() {
		return multipart;
	}
}
