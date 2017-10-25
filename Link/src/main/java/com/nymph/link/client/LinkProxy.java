package com.nymph.link.client;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nymph.link.model.Request;
import com.nymph.link.model.Response;
import com.nymph.link.registry.ServiceDiscovery;
import com.nymph.link.utils.StringUtil;

/**
 *用于创建 服务代理
 *
 * @author Nymph
 */
public class LinkProxy {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinkProxy.class);

    private String serviceAddress;

    private ServiceDiscovery serviceDiscovery;

    public LinkProxy(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    public LinkProxy(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    public <T> T create(final Class<?> interfaceClass) {
        return create(interfaceClass, "");
    }

    @SuppressWarnings("unchecked")
    public <T> T create(final Class<?> interfaceClass, final String serviceVersion) {
        // 创建动态代理对象
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        // 创建 Link 请求对象并设置请求属性
                        Request request = new Request();
                        request.setRequestId(UUID.randomUUID().toString());
                        request.setInterfaceName(method.getDeclaringClass().getName());
                        request.setServiceVersion(serviceVersion);
                        request.setMethodName(method.getName());
                        request.setParameterTypes(method.getParameterTypes());
                        request.setParameters(args);
                        // 获取服务地址
                        if (serviceDiscovery != null) {
                            String serviceName = interfaceClass.getName();
                            if (StringUtil.isNotEmpty(serviceVersion)) {
                                serviceName += "-" + serviceVersion;
                            }
                            serviceAddress = serviceDiscovery.discover(serviceName);
                            LOGGER.debug("discover service: {} => {}", serviceName, serviceAddress);
                        }
                        if (StringUtil.isEmpty(serviceAddress)) {
                            throw new RuntimeException("server address is empty");
                        }
                        // 从 Link服务地址中解析主机名与端口号
                        String[] array = StringUtil.split(serviceAddress, ":");
                        String host = array[0];
                        int port = Integer.parseInt(array[1]);
                        // 创建 Link 客户端对象并发送 Link 请求
                        Client client = new Client(host, port);
                        long time = System.currentTimeMillis();
                        Response response = client.send(request);
                        LOGGER.debug("time: {}ms", System.currentTimeMillis() - time);
                        if (response == null) {
                            throw new RuntimeException("response is NULL");
                        }
                        // 返回 Link 响应结果
                        if (response.hasException()) {
                            throw response.getException();
                        } else {
                            return response.getResult();
                        }
                    }
                }
        );
    }
}
