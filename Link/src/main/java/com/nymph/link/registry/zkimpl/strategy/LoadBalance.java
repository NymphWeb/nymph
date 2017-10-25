package com.nymph.link.registry.zkimpl.strategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


/**
 *  负载均衡类，参考了Dubbo的源代码
 * 	@author Nymph
 */
public class LoadBalance {

	/**
	 * 存储节点->权重 
	 */
	private static Map<String, Integer> invoker = new HashMap<String, Integer>();
	
	public static void add(String address, int weight){
		invoker.put(address, weight);
	}
	
	public static void delete(String address){
		invoker.remove(address);
	}
	
	/**
	 * 随机负载均衡策略，参考了Dubbo的随机策略 
	 */
	public static String Random_LoadBalance(List<String> invokers){
		int length = invokers.size();
		int totalWeight = 0;
		boolean sameWeight = true;
		Random random = new Random();
		for(int i=0; i<length; ++i){
			int weight = invoker.get(invokers.get(i));
			totalWeight += weight;
			if(sameWeight && i>0 && weight!=invoker.get(invokers.get(i-1))){
				sameWeight = false;
			}
		}
		
		if(totalWeight > 0 && sameWeight){
			
			//如果权重不相同且权重大于0则按总权重数随机 
			int offset = random.nextInt(totalWeight);
			//确定随机值在哪个片上
			for(int i=0; i<length; i++){
				offset -= invoker.get(invokers.get(i));
				if(offset < 0){
					return invokers.get(i);
				}
			}
		}
		
		// 如果权重相同或权重为0则均等随机  
	   return invokers.get(random.nextInt(length));
	}
	
}
