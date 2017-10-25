package com.nymph.bean.component;

import java.util.Map;

/**
 * bean的注册接口
 */
public interface BeansRegister {

    /**
     * 将bean的集合注册到bean工厂
     * @return  存放bean的map集合, key应该为bean的名称, value为bean实例
     */
    Map<String, Object> registerBeans();
}
