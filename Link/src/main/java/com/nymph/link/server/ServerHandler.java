package com.nymph.link.server;


import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nymph.link.model.Request;
import com.nymph.link.model.Response;
import com.nymph.link.utils.StringUtil;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;

/**
 * Link 服务端处理器（用于处理 Request）
 *
 * @author Nymph
 */
public class ServerHandler extends SimpleChannelInboundHandler<Request> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerHandler.class);

    private final Map<String, Object> handlerMap;

    public ServerHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    @Override
    public void channelRead0(final ChannelHandlerContext ctx, Request request) throws Exception {
        Server.submit(new Runnable(){
			@Override
			public void run() {
				// 创建并初始化Response
		        Response response = new Response();
		        response.setRequestId(request.getRequestId());
		        try {
		            Object result = handle(request);
		            response.setResult(result);
		        } catch (Exception e) {
		            LOGGER.error("handle result failure", e);
		            response.setException(e);
		        }
		        // 写入Response并自动关闭连接
		        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);				
			}
        	
        });
    	
    }
    /**
     * 使用request获取服务接口和服务版本,并反射执行调用
     * @param request
     * @return
     * @throws Exception
     */
    private Object handle(Request request) throws Exception {
        // 获取服务对象
        String serviceName = request.getInterfaceName();
        String serviceVersion = request.getServiceVersion();
        if (StringUtil.isNotEmpty(serviceVersion)) {
            serviceName += "-" + serviceVersion;
        }
        Object serviceBean = handlerMap.get(serviceName);
        if (serviceBean == null) {
            throw new RuntimeException(String.format("can not find service bean by key: %s", serviceName));
        }
        // 获取反射调用所需的参数
        Class<?> serviceClass = serviceBean.getClass();
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();
        // 使用 CGLib 执行反射调用
        FastClass serviceFastClass = FastClass.create(serviceClass);
        FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName, parameterTypes);
        return serviceFastMethod.invoke(serviceBean, parameters);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.error("server caught exception", cause);
        ctx.close();
    }
}
