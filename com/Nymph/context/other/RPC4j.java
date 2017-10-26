package com.nymph.context.other;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
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
	
	public RPC4j(String host, int port, String charset, int bufferSize) {
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
	
	public RPC4j(String host, int port) {
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
		byte[] bs = doAccess(path, "GET", null);
		return getResponse(bs);
	}
	/**
	 * 发出一个Post请求并且返回响应结果
	 * @param path	请求的url路径
	 * @param form	表示表单数据的数组(每个元素的格式为key=value)
	 * @return		服务器响应的结果
	 */
	public String doPost(String path, String... form) {
		byte[] bs = doAccess(path, "POST", format(form));
		return getResponse(bs);
	}
	/**
	 * 发出一个Put请求并且返回响应结果
	 * @param path	请求的url路径
	 * @param form	表示表单数据的数组(每个元素的格式为key=value)
	 * @return		服务器响应的结果
	 */
	public String doPut(String path, String... form) {
		byte[] bs = doAccess(path, "PUT", format(form));
		return getResponse(bs);
	}
	/**
	 * 发出一个Delete请求并且返回响应结果
	 * @param path	请求的url路径
	 * @param form	表示表单数据的数组(每个元素的格式为key=value)
	 * @return		服务器响应的结果
	 */
	public String doDelete(String path, String... form) {
		byte[] bs = doAccess(path, "DELETE", format(form));
		return getResponse(bs);
	}
	
	public Object getObject(String path) throws IOException, ClassNotFoundException {
		byte[] bs = doAccess(path, "GET", null);
		for (int i = 0; i < bs.length; i++) {
			if (i > 0 && bs[i - 1] == '\n' && bs[i] == '\r' && bs[i + 1] == '\n') {
				byte[] bytes = new byte[bs.length - (i + 2)];
				System.arraycopy(bs, i + 2, bytes, 0, bytes.length);
				try(ObjectInputStream inputStream = 
					new ObjectInputStream(new ByteArrayInputStream(bytes));) {
					return inputStream.readObject();
				}
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
		String result = new String(bytes);
		return result.substring(result.indexOf("\n\r\n") + 3);
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
	 * @param method	请求方式
	 * @param params	表单数据
	 * @return			响应结果
	 */
	private byte[] doAccess(String path, String method, String params) {
		try {
			long length = 0;
			String param = "";
			if (params != null) {
				param = params;
				length = param.getBytes(charset).length;
			}
			// 初始化请求头信息
			String header = initilizedRequestHeader(method, path, length, params);
				
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
		return new StringBuilder().append(method + " " + path)
			.append(" HTTP/1.1\n")
			.append("Host:"+ host +":"+ port +"\n")
			.append("Content-Type: application/x-www-form-urlencoded;charset=UTF-8\n")
			.append("User-Agent: NymphRPC/1.0\n")
			.append("Accept: */*\n")
			.append("Content-Length: "+ length +"\n")
			.append("Accept-Encoding: gzip, deflate\n")
			.append("Connection: keep-alive\n\r\n")
			.append(param).toString();
	}
	
	public static void main(String[] args) throws ClassNotFoundException, IOException {
		RPC4j rpc4j = new RPC4j("127.0.0.1", 9900);
		System.out.println(rpc4j.getObject("/class"));
	}
}
