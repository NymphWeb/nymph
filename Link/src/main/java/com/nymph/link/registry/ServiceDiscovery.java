package com.nymph.link.registry;

/**
 * 服务发现接口
 *
 * @author Nymph
 */
public interface ServiceDiscovery {

    /**
     * 根据服务名称查找服务地址
     *
     * @param serviceName 服务名称
     * @return 服务地址
     */
    String discover(String serviceName);
}