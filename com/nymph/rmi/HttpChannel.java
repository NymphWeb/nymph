package com.nymph.rmi;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * 发出Http请求并获取服务端响应的工具
 * @author LiuYang
 * @author LiangTianDong
 * @date 2017年10月19日下午9:18:10
 */
public class HttpChannel {
	private static final String PARAM_POSITION = "\n\r\n";
	
	// 套接字管道
	private SocketChannel socketChannel;
	
	// 字节缓冲区
	private ByteBuffer byteBuffer;
	
	// 编码格式
	private Charset charset;
	
	// 端口号
	private int port;
	
	// 主机名 或者叫 ip
	private String host;
	
	public HttpChannel(String host, int port, String charset, int bufferSize) {
		try {
			this.port = port;
			this.host = host;
			this.charset = Charset.forName(charset);
			byteBuffer = ByteBuffer.allocate(bufferSize);
			socketChannel = SocketChannel.open(new InetSocketAddress(host, port));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public HttpChannel(String host, int port) {
		try {
			this.port = port;
			this.host = host;
			this.charset = Charset.forName("UTF-8");
			byteBuffer = ByteBuffer.allocate(1024 * 10);
			socketChannel = SocketChannel.open(new InetSocketAddress(host, port));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 发出一个Get请求并且返回响应结果
	 * @param path	请求的url路径
	 * @return		服务器响应的结果
	 */
	public String doGet(String path) {
		byte[] bs = doAccess(path, Pattern.GET, null);
		return getResponse(bs);
	}
	/**
	 * 发出一个Post请求并且返回响应结果
	 * @param path	请求的url路径
	 * @param form	表示表单数据的数组(每个元素的格式为key=value)
	 * @return		服务器响应的结果
	 */
	public String doPost(String path, String... form) {
		byte[] bs = doAccess(path, Pattern.POST, format(form));
		return getResponse(bs);
	}
	/**
	 * 发出一个Put请求并且返回响应结果
	 * @param path	请求的url路径
	 * @param form	表示表单数据的数组(每个元素的格式为key=value)
	 * @return		服务器响应的结果
	 */
	public String doPut(String path, String... form) {
		byte[] bs = doAccess(path, Pattern.PUT, format(form));
		return getResponse(bs);
	}
	/**
	 * 发出一个Delete请求并且返回响应结果
	 * @param path	请求的url路径
	 * @param form	表示表单数据的数组(每个元素的格式为key=value)
	 * @return		服务器响应的结果
	 */
	public String doDelete(String path, String... form) {
		byte[] bs = doAccess(path, Pattern.DELETE, format(form));
		return getResponse(bs);
	}
	
	/**
	 * 获取服务端响应的序列化对象
	 * @param path		请求的路径
	 * @param pattern 	请求方式
	 * @return			获取的对象
	 */
	public Object getObject(String path, Pattern pattern) {
		byte[] bs = doAccess(path, pattern, null);
		// 截取响应头中的字节信息
		for (int i = 0; i < bs.length; i++) {
			if (i >= 3 && bs[i - 1] == '\n' && bs[i - 2] == '\r' && bs[i - 3] == '\n') {
				return Serializables.deSerialize(bs, i, bs.length);
			}
		}
		return null;
	}
	
	/**
	 * 关闭socketChannel
	 */
	public void close() {
		try {
			socketChannel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取字节数组中的响应结果
	 * @param bytes
	 * @return
	 */
	private String getResponse(byte[] bytes) {
		String result = new String(bytes, charset);
		return result.substring(result.indexOf(PARAM_POSITION) + 3);
	}
	
	/**
	 * 将数组格式化成http协议需要的格式
	 * @param form
	 * @return
	 */
	private String format(String[] form) {
		return Arrays.toString(form).replaceAll("[\\[\\]]", "").replace("=", "&");
	}
	
	/**
	 * 请求目标服务器上的指定资源, 并获取响应数据
	 * @param path		服务器的资源路径(url)
	 * @param pattern	请求方式
	 * @param params	表单数据
	 * @return			响应结果
	 */
	private byte[] doAccess(String path, Pattern pattern, String params) {
		try {
			long length = 0;
			String param = "";
			if (params != null) {
				param = params;
				length = param.getBytes(charset).length;
			}
			// 初始化请求头信息
			String header = initilizedRequestHeader(pattern.name(), path, length, params);
				
			synchronized (this) {
				socketChannel.write(ByteBuffer.wrap(header.getBytes(charset)));
				int len = socketChannel.read(byteBuffer);
				byteBuffer.flip();
				byte[] bytes = new byte[len];
				System.arraycopy(byteBuffer.array(), 0, bytes, 0, len);
				byteBuffer.clear();
				return bytes;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 初始化请求头信息
	 */
	private String initilizedRequestHeader(String method, String path, long length, String param) {
		return new StringBuilder()
			.append(method)
			.append(" ")
			.append(path)
			.append(" ")
			.append("HTTP/1.1\n")
			.append("Host:")
			.append(host)
			.append(":")
			.append(port)
			.append("\n")
			.append("User-Agent: liu\n")
			.append("Accept: */*\n")
			.append("Content-Length: ")
			.append(length)
			.append("\n")
			.append("Accept-Encoding: gzip, deflate\n")
			.append("Connection: keep-alive\n\r\n")
			.append(param)
			.toString();
	}
}
