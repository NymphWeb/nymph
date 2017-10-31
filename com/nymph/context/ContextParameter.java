package com.nymph.context;

import java.util.Map;

import com.nymph.bean.web.MapperInfoContainer.MapperInfo;
import com.nymph.transfer.Multipart;
/**
 * 参数解析器将要解析的对象
 * @author NYMPH
 * @date 2017年9月21日下午8:16:00
 */
public class ContextParameter {

	private Map<String, String[]> params;
	
	private ContextWrapper context;
	
	private Multipart multipart;
	
	private MapperInfo mapperInfo;
	
	public ContextParameter(MapperInfo mapperInfo,
					Map<String, String[]> params,
					ContextWrapper context,
					Multipart multipart) {
		
		this.mapperInfo = mapperInfo;
		this.context = context;
		this.params = params;
		this.multipart = multipart;
	}
	
	public MapperInfo getMapperInfo() {
		return mapperInfo;
	}

	public String getPlaceHolder(String url) {
		return mapperInfo.getPlaceHolder().get(url);
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
