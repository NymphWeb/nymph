package com.nymph.context.model;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * 简单的对服务器发出请求的工具
 * @author LiuYang
 * @author LiangTianDong
 * @date 2017年10月19日下午9:18:10
 */
public class RPC4j {
	
	// 请求头信息
	private String header;
	
	// 套接字管道
	private SocketChannel socketChannel;
	
	// 字节缓冲区
	private ByteBuffer byteBuffer;
	
	// 编码格式
	private Charset charset;
	
	public RPC4j(String host, int port, String charset, int bufferSize) {
		try {
			this.charset = Charset.forName(charset);
			byteBuffer = ByteBuffer.allocate(bufferSize);
			socketChannel = SocketChannel.open(new InetSocketAddress(host, port));
			initilizedRequestHeader(host, port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public RPC4j(String host, int port) {
		try {
			this.charset = Charset.forName("UTF-8");
			byteBuffer = ByteBuffer.allocate(1024 * 10);
			socketChannel = SocketChannel.open(new InetSocketAddress(host, port));
			initilizedRequestHeader(host, port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public String doGet(String path, Paramter params) {
		return doAccess(path, "GET", params.sb.substring(1));
	}
	
	public String doGet(String path) {
		return doAccess(path, "GET", null);
	}
	
	public String doPost(String path, Paramter params) {
		return doAccess(path, "POST", params.sb.substring(1));
	}
	
	public String doPost(String path) {
		return doAccess(path, "POST", null);
	}
	
	public String doPut(String path, Paramter params) {
		return doAccess(path, "PUT", params.sb.substring(1));
	}
	
	public String doPut(String path) {
		return doAccess(path, "PUT", null);
	}
	
	public String doDelete(String path, Paramter params) {
		return doAccess(path, "DELETE", params.sb.substring(1));
	}
	
	public String doDelete(String path) {
		return doAccess(path, "DELETE", null);
	}
	
	public void close() {
		try {
			socketChannel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 发起一次请求, 建议new对象来操作
	 * @param ipAddress	ip地址(浏览器地址栏的完整地址)
	 * @param method	请求方式
	 * @param params	表单数据(参数为 key=value 这种形式)
	 * @return
	 */
	public static String access(String ipAddress, String method, String... params) {
		int slashs = ipAddress.indexOf("//");
		int slash = ipAddress.indexOf("/", slashs = slashs == -1 ? 0 : slashs + 2);
		String path = ipAddress.substring(slash);
		
		String[] split = ipAddress.substring(slashs, slash).split(":");
		String host = split[0];
		int port = Integer.parseInt(split[1]);
		
		RPC4j rPC4j = new RPC4j(host, port);
		
		String paramString = null;
		if (params != null) {
			paramString = Arrays.toString(params).replaceAll("[\\[\\]]", "").replace(", ", "&");
		}
		String result = rPC4j.doAccess(path, method.toUpperCase(), paramString);
		rPC4j.close();
		return result;
	}
	
	/**
	 * 请求目标服务器上的指定资源, 并获取响应数据
	 * @param path		服务器的资源路径(url)
	 * @param method	请求方式
	 * @param params	表单数据
	 * @return			响应结果
	 */
	private String doAccess(String path, String method, String params) {
		try {
			long length = 0;
			String param = "";
			if (params != null) {
				param = params;
				length = param.getBytes(charset).length;
			}
			
			String head = header.replace("@path@", String.format("%s %s", method, path))
					.replace("@length@", Long.toString(length)) + param;
				
			synchronized (this) {
				socketChannel.write(ByteBuffer.wrap(head.getBytes(charset)));
				int read = socketChannel.read(byteBuffer);
				byteBuffer.flip();
				String result = new String(byteBuffer.array(), 0, read);
				byteBuffer.clear();
				return result.substring(result.indexOf("\n\r\n") + 3);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 初始化请求头信息
	 * @param host  IP地址
	 * @param port	端口号
	 */
	private void initilizedRequestHeader(String host, int port) {
		StringBuilder builder = new StringBuilder();
		header = builder.append("@path@ HTTP/1.1\n")
			.append("Host:"+ host +":"+ port +"\n")
			.append("Content-Type: application/x-www-form-urlencoded\n")
			.append("User-Agent: NymphVisitor/1.0\n")
			.append("Accept: */*\n")
			.append("Content-Length: @length@\n")
			.append("Accept-Encoding: gzip, deflate\n")
			.append("Connection: keep-alive\n\r\n").toString();
	}

	public static class Paramter {
		private StringBuilder sb;
		
		public Paramter() {
			sb = new StringBuilder();
		}
		
		public Paramter put(String name, String value) {
			sb.append("&").append(name).append("=").append(value);
			return this;
		}
	}

}
