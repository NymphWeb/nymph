package com.nymph.bean;

import java.util.Map;

/**
 * bean的注册接口
 */
public interface BeansRegister {

    /**
     * 将bean注册到bean工厂
     * @return  存放bean的map集合, key应该为bean的名称, value为bean本身
     */
    Map<String, Object> registerBeans();
}
