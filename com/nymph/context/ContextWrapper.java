package com.nymph.context;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nymph.rmi.Serializables;
import com.nymph.utils.BasicUtil;
import com.nymph.utils.PageCSS;
import com.nymph.utils.StrUtil;

/**
 * Servlet3.0之后的异步请求上下文的一个包装类
 * @author LiuYang
 * @author LiangTianDong
 * @date 2017年9月21日下午4:58:31
 */
public final class ContextWrapper {
	private static final Logger LOGGER = LoggerFactory.getLogger(ContextWrapper.class);

	private AsyncContext context;

	private HttpServletRequest request;

	private HttpServletResponse response;
	/**
	 *  发送HTML
	 */
	private static final String CONTENT_TYPE_HTML = "text/html;charset=UTF-8";
	/**
	 *  发送JSON数据
	 */
	private static final String CONTENT_TYPE_JSON = "application/json;charset=UTF-8";
	/**
	 *  发送文本
	 */
	private static final String CONTENT_TYPE_TEXT = "text/plain;charset=UTF-8";
	/**
	 *  文件下载
	 */
	private static final String CONTENT_TYPE_STREAM = "application/octet-stream;charset=UTF-8";
	
	public ContextWrapper(AsyncContext context) {
		this.context = context;
		this.request = (HttpServletRequest) context.getRequest();
		this.response = (HttpServletResponse) context.getResponse();
		setTimeout(10000);
	}

	public void setTimeout(long mills) {
		context.setTimeout(mills);
	}

	public AsyncContext getAsyncContext() {
		return context;
	}
	
	public HttpServletResponse httpResponse() {
		return response;
	}

	public HttpServletRequest httpRequest() {
		return request;
	}
	
	public void commit() {
		context.complete();
	}
	/**
	 * 获取当前请求的所有参数
	 * @return
	 */
	public Map<String, String[]> getParameters() {
		return request.getParameterMap();
	}
	/**
	 * 获取当前请求的contentType
	 * @return
	 */
	public String contentType() {
		return StrUtil.emptyString(request.getContentType());
	}
	/**
	 * 获取当前请求的contextPath
	 * @return
	 */
	public String contextPath() {
		return StrUtil.emptyString(request.getContextPath());
	}
	/**
	 * 获取当前请求的Uri
	 * @return
	 */
	public String getUri() {
		return StrUtil.emptyString(request.getRequestURI());
	}
	/**
	 * 获取当前请求的Url
	 * @return
	 */
	public String getUrl() {
		StringBuffer url = request.getRequestURL();
		return url == null ? "" : url.toString();
	}
	/**
	 * 以相对路径重定向
	 * @param location
	 */
	public void relativeRedirect(String location) {
		try {
			response.sendRedirect(contextPath() + location);
		} catch (IOException e) {
			LOGGER.error(null, e);
		}
	}
	/**
	 * 以绝对路径重定向(如"http://192.168.0.0/demo"这种格式)
	 * @param location
	 */
	public void absoluteRedirect(String location) {
		response.setStatus(301);
		response.addHeader("Location", location);
	}
	/**
	 * 请求转发
	 * @param location
	 */
	public void forward(String location) {
		try {
			request.getRequestDispatcher(location).forward(request, response);
		} catch (Exception e) {
			LOGGER.error(null, e);
		}
	}
	/**
	 * 发送错误页面
	 * @param message
	 */
	public void sendError(String message) {
		response.setStatus(500);
		write(PageCSS.TOP + message, CONTENT_TYPE_HTML);
	}
	/**
	 * 发送servlet自带的404页面
	 * @param message
	 */
	public void send404(String message) {
		response.setStatus(404);
		write(PageCSS.TOP + message, CONTENT_TYPE_HTML);
	}
	/**
	 * 发送json数据
	 * @param info
	 */
	public void sendJSON(Object info) {
		write(info, CONTENT_TYPE_JSON);
	}
	/**
	 * 发送文本
	 * @param info
	 */
	public void sendText(Object info) {
		write(info, CONTENT_TYPE_TEXT);
	}

	/**
	 * 往页面写入数据
	 * @param info
	 * @param type
	 */
	private void write(Object info, String type) {
		try {
			response.setContentType(type);
			PrintWriter writer = response.getWriter();
			writer.print(info);
		} catch (IOException e) {
			LOGGER.error(null, e);
		}
	}
	
	/**
	 * 发送一个文件
	 * @param inputStream	文件的流
	 * @param filename		文件的名称
	 */
	public void sendFile(FileInputStream inputStream, String filename) {
		FileChannel channel = null;
		try {
			response.setContentType(CONTENT_TYPE_STREAM);
			response.addHeader("Content-Disposition", "attachment;filename=" + filename);
			ServletOutputStream outputStream = response.getOutputStream();
			channel = inputStream.getChannel();
			channel.transferTo(0, channel.size(), Channels.newChannel(outputStream));
		} catch (IOException e) {
			LOGGER.error(null, e);
		} finally {
			BasicUtil.closed(channel, inputStream);
		}
	}
	
	/**
	 * 发送一个实现了Serializable的对象
	 * @param serializable
	 * @throws IOException
	 */
	public void sendObject(Object serializable) throws IOException {
		serializableCheck(serializable);
		byte[] serialize = Serializables.serialize(serializable);
		response.getOutputStream().write(serialize);
	}
	
	/**
	 * 序列化检查
	 * @param object
	 */
	private void serializableCheck(Object object) {
		if (!(object instanceof Serializable)) {
			throw new IllegalArgumentException("返回的对象必须实现序列化");
		}
	}
}
