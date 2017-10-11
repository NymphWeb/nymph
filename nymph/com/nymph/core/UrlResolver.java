package com.nymph.core;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.nymph.bean.impl.HttpBean;

/**
 * @comment url解析器
 * @author NYMPH
 * @date 2017年9月26日2017年9月26日
 */
public interface UrlResolver extends Resolver{
	
	/**
	 * 当Content-Type为上传文件的类型时进行的处理
	 * @param request		当前的请求对象
	 * @return				请求中所有参数的map
	 * @throws Exception	FileUploadExcaption异常, 和UnsupportedEncodingException编码异常
	 */
	Map<String, String[]> multipartHandler(HttpServletRequest request) throws Exception;
	/**
	 * 寻找和指定url匹配的类映射信息, 这里需要考虑是否使用了@PlaceHolder占位符注解, 如果没有则直接从HttpBeanContainer
	 *     中获取, 否则需要遍历整个容器来寻找是否有对应的HttpBean, 并且需要获取到@PlaceHolder的value方法对应的值
	 * @param url	   		浏览器输入的url
	 * @return		 		url映射的HttpBean
	 * @throws Exception	这里使用到了clone, 可能抛出克隆方法的异常
	 */
	HttpBean placeHolderHandler(String url) throws Exception;

}
