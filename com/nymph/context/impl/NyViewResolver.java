package com.nymph.context.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nymph.context.ViewResolver;
import com.nymph.context.model.NyView;
import com.nymph.json.Jsons;
/**
 * @说明: 视图解析器的实现
 * @时间: 2017年9月17日
 * @作者: Nymph
 */
public class NyViewResolver extends AbstractResolver implements ViewResolver {
	private static final Logger LOGGER = LoggerFactory.getLogger(NyViewResolver.class);
	/** 
	 * HttpBean的返回值后缀 
	 */
	private static final String PATH_SUFFIX = configuration.getWebConfig().getSuffix();
	/** 
	 * HttpBean的返回值前缀
	 */
	private static final String PATH_PREFIX = configuration.getWebConfig().getPrefix();
	/** 
	 * 视图参数 
	 */
	private NyView nyView;
	/**
	 *  表示web层方法返回结果的类型 分下面四种
	 */
	private int typeCode;
	/**
	 *  表示转发请求
	 */
	private static final int REQUEST_FORWRAD = 1;
	/**
	 *  请求重定向(相对路径)
	 */
	private static final int RELATIVE_REDIRECT = 2;
	/**
	 *  请求重定向(绝对路径)
	 */
	private static final int ABSOLUTE_REDIRECT = 3;
	/**
	 *  当返回的是一个基本类型和String之外的其他对象时, 那么会将
	 * 			这个对象解析为json字符串
	 */
	private static final int RESPONSE_JSON = 4;
	/**
	 *  当目标方法的返回值是个String时, 即使这个字符串是json格式
	 * 			这里也并不会将它当Json处理, 而是直接原封不动的响应给页面
	 */
	private static final int RESPONSE_TEXT = 5;
	
	public NyViewResolver(NyView nyView) {
		super(nyView.getContext());
		this.nyView = nyView;
	}
	
	@Override
	public void resolver() throws Throwable {
		String result = dispatchTypeHandle(nyView.getReturnResult());
		
		switch (typeCode) {
		case REQUEST_FORWRAD:
			wrapper.forward(result);
			break;
		case RELATIVE_REDIRECT:
			wrapper.relativeRedirect(result);
			break;
		case ABSOLUTE_REDIRECT:
			wrapper.absoluteRedirect(result);
			break;
		case RESPONSE_JSON:
			wrapper.sendJSON(result);
			break;
		case RESPONSE_TEXT:
			wrapper.sendText(result);
			break;
		default:
			LOGGER.error("未知的转发方式");
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("ViewResolver result: typeCode[{}] - {}", typeCode, result);
		}
		wrapper.commit();
	}
	
	@Override
	public String dispatchTypeHandle(Object result) throws Exception {
		// 返回json是进行的处理
		if (nyView.getIsJSON()) {
			// 目标方法的返回值是除基本类型和String的其他类型时会将这个对象解析成Json字符串
			if (!(result instanceof String || result == null)) {
				typeCode = RESPONSE_JSON;
				return Jsons.resolve(result, nyView.getDateFormat());
			}
			// 当目标方法的返回值是String时不会解析成Json, 而是直接响应
			typeCode = RESPONSE_TEXT;
			return String.valueOf(result);
		}
		
		// 当想转发请求或重定向时的处理
		// '->'表示普通的重定向 '-->'表示以整个url路径来重定向
		String url = String.valueOf(result);
		if (url.startsWith("->")) {
			typeCode = RELATIVE_REDIRECT;
			url = url.replace("->", "");
			url = url.startsWith("/") ? url : "/" + url;
			
		} else if (url.startsWith("-->")) {
			typeCode = ABSOLUTE_REDIRECT;
			url = url.replace("-->", "");
			
		} else {
			typeCode = REQUEST_FORWRAD;
			url = url.startsWith("/") ? url : "/" + url;
			url = PATH_PREFIX + url + PATH_SUFFIX;
		}
		return url;
	}

}
