package com.nymph.interceptor;

import com.nymph.context.ContextWrapper;
/**
 * 标准的拦截器,实现多个时会形成一个拦截器链, 通过实现{@link #getOrder()}来指定
 *     拦截器链的执行顺序, 值越小优先级越高
 * @date 2017年9月17日下午2:47:33
 * @author liuYang
 * @author liangTanDong
 */
public interface NyInterceptors extends Comparable<NyInterceptors> {
	/**
	 * 被拦截的方法之前会执行此方法
	 * @param asyncContext 异步Context,可以方法执行之前对request和response进行操作
	 * @return <code>true</code>表示放行, <code>false</code>表示拦截
	 */
	boolean preHandle(ContextWrapper asyncContext);
	/**
	 * 被拦截的方法之后会执行此方法
	 * @param asyncContext 异步Context,可以在方法执行之后对request和response进行操作
	 */
	void behindHandle(ContextWrapper asyncContext);
	/**
	 * 当实现多个拦截器, 形成拦截器链时, 重写这个方法可以保证拦截器链的执行顺序
	 * 		   值越小的越会优先执行
	 * @return 返回一个代表执行顺序的int值
	 */
	int getOrder();
	
	default int compareTo(NyInterceptors o) {
		return this.getOrder() - o.getOrder();
	}
}

