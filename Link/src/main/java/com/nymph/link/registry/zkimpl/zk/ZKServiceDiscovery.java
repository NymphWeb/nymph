package com.nymph.link.registry.zkimpl.zk;


import java.util.List;

import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nymph.link.registry.ServiceDiscovery;
import com.nymph.link.registry.zkimpl.constant.Constant;
import com.nymph.link.registry.zkimpl.strategy.LoadBalance;
import com.nymph.link.utils.CollectionUtil;

/**
 * 基于 ZooKeeper 的服务发现接口实现
 *
 * @author Nymph
 */
public class ZKServiceDiscovery implements ServiceDiscovery {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZKServiceDiscovery.class);

    private String zkAddress;

    public ZKServiceDiscovery(String zkAddress) {
        this.zkAddress = zkAddress;
    }

    @Override
    public String discover(String name) {
        // 创建 ZooKeeper 客户端
        ZkClient zkClient = new ZkClient(zkAddress, Constant.ZK_SESSION_TIMEOUT, Constant.ZK_CONNECTION_TIMEOUT);
        LOGGER.debug("连接至 :"+ zkAddress +"主机的 zookeeper服务器");
        try {
            // 获取 service 节点
            String servicePath = Constant.ZK_REGISTRY_PATH + "/" + name;
            if (!zkClient.exists(servicePath)) {
                throw new RuntimeException(String.format("can not find any service node on path: %s", servicePath));
            }
            List<String> addressList = zkClient.getChildren(servicePath);
            if (CollectionUtil.isEmpty(addressList)) {
                throw new RuntimeException(String.format("can not find any address node on path: %s", servicePath));
            }
            // 获取 address 节点
            String address;
            int size = addressList.size();
            if (size == 1) {
                // 若只有一个地址，则获取该地址
                address = addressList.get(0);
                LOGGER.debug("仅仅发现一个节点: {}", address);
            } else {
                // 若存在多个地址，则根据负载均衡算法获取一个地址
                address = LoadBalance.Random_LoadBalance(addressList);
                LOGGER.debug("使用策略算法拉取到了: {}", address);
            }
            // 获取 address 节点的值
            String addressPath = servicePath + "/" + address;
            return zkClient.readData(addressPath);
        } finally {
            zkClient.close();
        }
    }
}