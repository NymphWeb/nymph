package com.nymph.link.client;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nymph.link.codec.Decoder;
import com.nymph.link.codec.Encoder;
import com.nymph.link.model.Request;
import com.nymph.link.model.Response;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 用于发送 Request请求
 *
 * @author Nymph
 */
public class Client extends SimpleChannelInboundHandler<Response> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);

    private final String host;
    private final int port;

    private Response response;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Response response) throws Exception {
        this.response = response;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("api caught exception", cause);
        ctx.close();
    }

    public Response send(Request request) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            // 创建并初始化 Netty 客户端 Bootstrap 对象
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel channel) throws Exception {
                    ChannelPipeline pipeline = channel.pipeline();
                    pipeline.addLast(new Encoder(Request.class)); // 编码 Link 请求
                    pipeline.addLast(new Decoder(Response.class)); // 解码 Link 响应
                    pipeline.addLast(Client.this); // 处理 Link 响应
                }
            });
            bootstrap.option(ChannelOption.TCP_NODELAY, true);
            // 连接 Link 服务器
            ChannelFuture future = bootstrap.connect(host, port).sync();
            // 写入 Link 请求数据并关闭连接
            Channel channel = future.channel();
            channel.writeAndFlush(request).sync();
            channel.closeFuture().sync();
            // 返回 Link 响应对象
            return response;
        } finally {
            group.shutdownGracefully();
        }
    }
}
