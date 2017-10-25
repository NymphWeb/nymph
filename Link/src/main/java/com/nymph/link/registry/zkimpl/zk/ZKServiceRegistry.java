package com.nymph.link.registry.zkimpl.zk;

import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nymph.link.registry.ServiceRegistry;
import com.nymph.link.registry.zkimpl.constant.Constant;
/**
 * 基于 ZooKeeper 的服务注册接口实现
 *
 * @author Nymph
 * @since 
 */
public class ZKServiceRegistry implements ServiceRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZKServiceRegistry.class);

    private final ZkClient zkClient;

    public ZKServiceRegistry(String zkAddress) {
        // 创建 ZooKeeper 客户端
        zkClient = new ZkClient(zkAddress, Constant.ZK_SESSION_TIMEOUT, Constant.ZK_CONNECTION_TIMEOUT);
        LOGGER.debug("成功连接至:"+zkAddress+" 的远程主机");
    }

    @Override
    public void register(String serviceName, String serviceAddress) {
        // 创建 registry 节点（持久）
        String registryPath = Constant.ZK_REGISTRY_PATH;
        if (!zkClient.exists(registryPath)) {
            zkClient.createPersistent(registryPath);
            LOGGER.debug("成功创建了节点: {}", registryPath);
        }
        // 创建 service 节点（持久）
        String servicePath = registryPath + "/" + serviceName;
        if (!zkClient.exists(servicePath)) {
            zkClient.createPersistent(servicePath);
            LOGGER.debug("成功创建了节点: {}", servicePath);
         System.out.println("成功创建了节点: {}"+ servicePath);
        }
        // 创建 address 节点（临时）
        String addressPath = servicePath + "/address-";
        String addressNode = zkClient.createEphemeralSequential(addressPath, serviceAddress);
       
        LOGGER.debug("成功创建了节点: {}", addressNode);
    }
}