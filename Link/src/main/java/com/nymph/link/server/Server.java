package com.nymph.link.server;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.nymph.link.annotation.Service;
import com.nymph.link.codec.Decoder;
import com.nymph.link.codec.Encoder;
import com.nymph.link.model.Request;
import com.nymph.link.model.Response;
import com.nymph.link.registry.ServiceRegistry;
import com.nymph.link.utils.StringUtil;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Link 服务器（用于发布 Link服务）
 *
 * @author Nymph
 */
public class Server implements ApplicationContextAware, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    private String serviceAddress;

    private ServiceRegistry serviceRegistry;
    
    private static ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(16);

    /**
     * 存放 服务名 与 服务对象 之间的映射关系
     */
    private Map<String, Object> handlerMap = new HashMap<>();

    public Server(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    public Server(String serviceAddress, ServiceRegistry serviceRegistry) {
        this.serviceAddress = serviceAddress;
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        // 扫描带有 Service 注解的类并初始化 handlerMap 对象
        Map<String, Object> serviceBeanMap = ctx.getBeansWithAnnotation(Service.class);
        if (MapUtils.isNotEmpty(serviceBeanMap)) {
            for (Object serviceBean : serviceBeanMap.values()) {
                Service linkService = serviceBean.getClass().getAnnotation(Service.class);
                String serviceName = linkService.value().getName();
                String serviceVersion = linkService.version();
                if (StringUtil.isNotEmpty(serviceVersion)) {
                    serviceName += "-" + serviceVersion;
                }
                handlerMap.put(serviceName, serviceBean);
            }
        }
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // 创建并初始化 Netty 服务端 Bootstrap 对象
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel channel) throws Exception {
                    ChannelPipeline pipeline = channel.pipeline();
                    pipeline.addLast(new Decoder(Request.class)); // 解码 RPC 请求
                    pipeline.addLast(new Encoder(Response.class)); // 编码 RPC 响应
                    pipeline.addLast(new ServerHandler(handlerMap)); // 处理 RPC 请求
                }
            });
            bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            // 获取 RPC 服务器的 IP 地址与端口号
            String[] addressArray = StringUtil.split(serviceAddress, ":");
            String ip = addressArray[0];
            int port = Integer.parseInt(addressArray[1]);
            // 启动 RPC 服务器
            ChannelFuture future = bootstrap.bind(ip, port).sync();
            // 注册 RPC 服务地址
            if (serviceRegistry != null) {
                for (String interfaceName : handlerMap.keySet()) {
                    serviceRegistry.register(interfaceName, serviceAddress);
                    LOGGER.debug("register service: {} => {}", interfaceName, serviceAddress);
                }
            }
            LOGGER.debug("server started on port {}", port);
            System.out.println("服务已经启动,位于本机 第 "+ port+ " 端口");
            // 关闭 RPC 服务器
            future.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
    
    /**
     * 提交任务 
     */
    public static void submit(Runnable task){
    	
        threadPoolExecutor.submit(task);
    }
}
