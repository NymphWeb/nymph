package com.nymph.bean;

/**
 * 在bean对象处理器, 可以对bean对象进行一系列操作	
 * @author NYMPH
 * @date 2017年10月5日下午2:55:01
 */
public interface BeansHandler {
	/**
	 * 在注册到bean容器之前的处理
	 * @param beansClass   bean的实例
	 * @return             表示最终要存入容器的对象
	 * @throws Exception 
	 */
	Object handlerBefore(Object bean);
	/**
	 * 在注册到bean容器之后的处理
	 * @param bean	bean的实例
	 */
	void handlerAfter(Object bean);

}
