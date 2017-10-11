package com.nymph.core.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nymph.utils.BasicUtils;
import com.nymph.utils.NymphCSS;
import com.nymph.utils.StrUtils;

/**
 * Servlet3.0之后的异步请求对象的一个包装类
 * @author LiuYang
 * @author LiangTianDong
 * @date 2017年9月21日下午4:58:31
 */
public final class AsyncRequestWrapper {

	private static final Logger LOGGER = LoggerFactory.getLogger(AsyncRequestWrapper.class);

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
	

	public AsyncRequestWrapper(AsyncContext context) {
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

	public String contentType() {
		return StrUtils.notNull(request.getContentType());
	}

	public String contextPath() {
		return StrUtils.notNull(request.getContextPath());
	}

	public HttpServletResponse httpResponse() {
		return response;
	}

	public HttpServletRequest httpRequest() {
		return request;
	}
	
	public void commit() {
		try {
			context.complete();
		} catch (Throwable e) {
			LOGGER.warn("asyncRequest 可能重复提交了");
		}
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
	 * 以绝对路径重定向
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
		} catch (ServletException | IOException e) {
			LOGGER.error(null, e);
		}
	}
	/**
	 * 发送错误页面
	 * @param message
	 */
	public void sendError(String message) {
		try {
			response.setContentType(CONTENT_TYPE_HTML);
			PrintWriter writer = response.getWriter();
			response.setStatus(500);
			writer.print(NymphCSS.TOP + message + NymphCSS.DOWN);
		} catch (IOException e) {
			LOGGER.error(null, e);
		}
	}
	/**
	 * 发送servlet自带的404页面
	 * @param message
	 */
	public void send404(String message) {
		try {
			response.sendError(404, message);
		} catch (IOException e) {
			LOGGER.error(null, e);
		}
	}
	/**
	 * 发送json数据
	 * @param info
	 */
	public void sendJSON(Object info) {
		try {
			response.setContentType(CONTENT_TYPE_JSON);
			PrintWriter writer = response.getWriter();
			writer.print(info);
		} catch (IOException e) {
			LOGGER.error(null, e);
		}
	}
	/**
	 * 发送文本
	 * @param info
	 */
	public void sendText(Object info) {
		try {
			response.setContentType(CONTENT_TYPE_TEXT);
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
			BasicUtils.closed(channel, inputStream);
		}
	}
}
