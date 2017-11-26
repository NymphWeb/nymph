package com.nymph.context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.nymph.context.core.ResovlerUrl;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nymph.bean.web.MapperInfoContainer;
import com.nymph.bean.web.MapperInfoContainer.MapperInfo;
import com.nymph.exception.NoSuchClassException;
import com.nymph.queue.NyQueue;
import com.nymph.transfer.Multipart;
import com.nymph.utils.StrUtil;

/**
 * url解析器实现
 * @author NYMPH
 * @date 2017年10月7日下午8:22:08
 */
public class ResolverUrlImpl extends AbstractResolver implements ResovlerUrl {
	private static final Logger LOGGER = LoggerFactory.getLogger(ResolverUrlImpl.class);
	/**
	 *  MapperInfo的容器
	 */
	private static MapperInfoContainer container = beansFactory.getHttpBeansContainer();
	/**
	 *  文件上传时需要的对象
	 */
	private Multipart multipart;
	/**
	 *  参数队列
	 */
	private NyQueue<ContextParameter> queue;
	
	public ResolverUrlImpl(ContextWrapper wrapper, NyQueue<ContextParameter> queue) {
		super(wrapper);
		this.queue = queue;
	}

	@Override
	public void resolver() throws Exception {
		String contextPath = wrapper.contextPath();
		String urlMapping = StrUtil.delete(wrapper.getUri(), contextPath);
		
		// 获取url映射的类和方法 
		MapperInfo httpBean = placeHolderHandler(urlMapping);
		urlMappingResloverCheck(httpBean);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("httpBeans: {}", container);
		}
		// 获取到request中所有的参数
		Map<String, String[]> params = judgeContentType();
		// 将处理好的参数对象放入队列
		queue.put(new ContextParameter(httpBean, params, wrapper, multipart));
	}
	
	@Override
	public MapperInfo placeHolderHandler(String url) throws Exception {
		MapperInfo mapperInfo = container.getMapperInfo(url);
		// 如果已经能从容器中取到url对应的映射对象则直接返回
		if (mapperInfo != null) 
			return mapperInfo;
		// 否则遍历httpMap中的url
		out: for (Entry<String, MapperInfo> kv : container.getAllMapperInfo()) {
			// 浏览器请求的地址
			String[] requestUrls = url.split("/");
			// MapperInfo中保存的地址
			String[] nativeUrls = kv.getKey().split("/");
			
			if (requestUrls.length == nativeUrls.length) {
				MapperInfo info = kv.getValue();
				// 存放占位符和对应值的Map
				Map<String, String> placeHolder = new HashMap<>();
				
				for (int i = 0; i < nativeUrls.length; i++) {
					if (nativeUrls[i].startsWith("@")) {
						// 将@xxx 的xxx为键 浏览器实际输入的字符串为值存入map中
						String keyVal = nativeUrls[i].substring(1);
						placeHolder.put(keyVal, requestUrls[i]);
					} else if (!requestUrls[i].equals(nativeUrls[i])) {
						continue out;
					}
				}
				// 由于并发问题这里如果是同一个对象的话会共享数据, 所以就克隆了一个
				return info.initialize(placeHolder);
			}
		}
		return null;
	}
	
	@Override
	public Map<String, String[]> multipartHandler() throws Exception {
		multipart = new Multipart();
		DiskFileItemFactory disk = new DiskFileItemFactory();
		ServletFileUpload servletFileUpload = new ServletFileUpload(disk);
		List<FileItem> items = servletFileUpload.parseRequest(wrapper.httpRequest());

		Map<String, String[]> params = new HashMap<>();

		for (FileItem fileItem : items) {
			if (fileItem.isFormField()) {
				String fieldName = fileItem.getFieldName();
				String fieldVal = fileItem.getString("UTF-8");
				String[] param = params.get(fieldName);
				
				if (param != null) {
					String[] newArray = new String[param.length + 1];
					System.arraycopy(param, 0, newArray, 0, param.length);
					newArray[param.length] = fieldVal;
					param = newArray;
				} else {
					param = new String[]{fieldVal};
				}
				params.put(fieldName, param);
			} 
			else {
				multipart.addFiles(fileItem);
			}
		}
		return params;
	}
	
	/**
	 * 判断请求的Content-Type
	 * @return 				表示请求中所有参数的map
	 * @throws Exception	multipartHandler方法抛出的异常
	 */
	private Map<String, String[]> judgeContentType() throws Exception {
		if (wrapper.contentType().startsWith("multipart/form-data")) {
			return multipartHandler();
		} else {
			return wrapper.getParameters();
		}
	}
	
	/**
	 * url映射检查, 如果匹配不到任何对象则抛出异常
	 * @param mapperInfo	匹配到的MapperInfo对象
	 */
	private void urlMappingResloverCheck(MapperInfo mapperInfo) {
		if (mapperInfo == null) {
			throw new NoSuchClassException(wrapper.getUri());
		}
	}
}
