package com.nymph.context.model;

import java.util.Map;

import com.nymph.bean.web.HttpBeansContainer.HttpBean;
import com.nymph.context.wrapper.ContextWrapper;
import com.nymph.transfer.Multipart;
/**
 * 参数解析器将要解析的对象
 * @author NYMPH
 * @date 2017年9月21日下午8:16:00
 */
public class NyParam {

	private Map<String, String[]> params;
	
	private ContextWrapper context;
	
	private Multipart multipart;
	
	private HttpBean httpBean;
	
	public NyParam(	HttpBean httpBean,
					Map<String, String[]> params,
					ContextWrapper context,
					Multipart multipart) {
		
		
		this.httpBean = httpBean;
		this.context = context;
		this.params = params;
		this.multipart = multipart;
	}
	
	public HttpBean getHttpBean() {
		return httpBean;
	}

	public String getPlaceHolder(String url) {
		return httpBean.getPlaceHolder().get(url);
	}

	public ContextWrapper getContext() {
		return context;
	}

	public Map<String, String[]> getParams() {
		return params;
	}

	public Multipart getMultipart() {
		return multipart;
	}
}