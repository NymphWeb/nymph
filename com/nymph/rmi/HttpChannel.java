package com.nymph.rmi;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * 发出Http请求并获取服务端响应的工具
 * @author LiuYang
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
    // 缓存区大小
    private int bufferSize;
    // 端口号
    private int port;
    // 主机名(ip)
    private String host;
    // nio事件选择器
    private Selector selector;

    public HttpChannel(String host, int port, String charset, int bufferSize) {
        this.port = port;
        this.host = host;
        this.bufferSize = bufferSize;
        this.charset = Charset.forName(charset);
        initConnect();
    }

    public HttpChannel(String host, int port) {
        this.port = port;
        this.host = host;
        this.bufferSize = 1024;
        this.charset = Charset.forName("UTF-8");
        initConnect();
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = Charset.forName(charset);
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    private void initConnect() {
        try {
            selector = Selector.open();
            byteBuffer = ByteBuffer.allocateDirect(bufferSize);
            socketChannel = SocketChannel.open(new InetSocketAddress(host, port));
            socketChannel.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发出一个Get请求并且返回响应结果
     *
     * @param path 请求的url路径
     * @return 服务器响应的结果
     */
    public String doGet(String path) {
        byte[] bs = doAccess(path, Pattern.GET, null);
        return getResponseData(bs);
    }

    /**
     * 发出一个Post请求并且返回响应结果
     *
     * @param path 请求的url路径
     * @param form 表示表单数据的数组(每个元素的格式为key=value)
     * @return 服务器响应的结果
     */
    public String doPost(String path, String... form) {
        byte[] bs = doAccess(path, Pattern.POST, format(form));
        return getResponseData(bs);
    }

    /**
     * 发出一个Put请求并且返回响应结果
     *
     * @param path 请求的url路径
     * @param form 表示表单数据的数组(每个元素的格式为key=value)
     * @return 服务器响应的结果
     */
    public String doPut(String path, String... form) {
        byte[] bs = doAccess(path, Pattern.PUT, format(form));
        return getResponseData(bs);
    }

    /**
     * 发出一个Delete请求并且返回响应结果
     *
     * @param path 请求的url路径
     * @param form 表示表单数据的数组(每个元素的格式为key=value)
     * @return 服务器响应的结果
     */
    public String doDelete(String path, String... form) {
        byte[] bs = doAccess(path, Pattern.DELETE, format(form));
        return getResponseData(bs);
    }

    /**
     * 获取服务端响应的序列化对象
     *
     * @param path    请求的路径
     * @param pattern 请求方式
     * @return 获取的对象
     */
    public Object getObject(String path, Pattern pattern) {
        byte[] bs = doAccess(path, pattern, null);
        // 截取响应头中的字节信息
        for (int i = 0; i < bs.length; i++) {
            if (i >= 3 && (bs[i - 1] == '\n' && bs[i - 2] == '\r' && bs[i - 3] == '\n')) {
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
     * 获取字节数组中的响应数据
     *
     * @param bytes
     * @return
     */
    private String getResponseData(byte[] bytes) {
        String result = new String(bytes, charset);
        return result.substring(result.indexOf(PARAM_POSITION) + 3);
    }

    /**
     * 将数组格式化成http协议需要的格式
     *
     * @param form
     * @return
     */
    private String format(String[] form) {
        return Arrays.toString(form).replaceAll("[\\[\\]]", "").replace("=", "&");
    }

    /**
     * 请求目标服务器上的指定资源, 并获取响应数据
     *
     * @param path    服务器的资源路径(url)
     * @param pattern 请求方式
     * @param params  表单数据
     * @return 响应结果
     */
    private byte[] doAccess(String path, Pattern pattern, String params) {
        try {
            long length = 0;
            String param = "";
            if (params != null) {
                param = params;
                length = param.getBytes(charset).length;
            }
            // 请求头信息
            String header = getRequestHeader(pattern.name(), path, length, param);

            byte[] bytes = header.getBytes(charset);
            readyWrite();
            byteBuffer.put(bytes);
            byteBuffer.rewind();
            byteBuffer.limit(bytes.length);
            socketChannel.write(byteBuffer);
            byteBuffer.clear();
            readyRead();
            int len = socketChannel.read(byteBuffer);
            byteBuffer.clear();
            return getBytes(len);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取byteBuffer中的数据
     *
     * @param length
     * @return
     */
    private byte[] getBytes(int length) {
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            bytes[i] = byteBuffer.get(i);
        }
        return bytes;
    }

    /**
     * 等待socketChannel到可写的状态
     *
     * @throws IOException
     */
    private void readyWrite() throws IOException {
        socketChannel.register(selector, SelectionKey.OP_WRITE);
        selector.select(3000);
    }

    /**
     * 等待socketChannel到可读的状态
     *
     * @throws IOException
     */
    private void readyRead() throws IOException {
        socketChannel.register(selector, SelectionKey.OP_READ);
        selector.select(3000);
    }

    /**
     * 获取请求头信息
     */
    private String getRequestHeader(String method, String path, long length, String param) {
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
