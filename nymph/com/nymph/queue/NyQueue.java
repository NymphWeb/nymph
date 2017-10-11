package com.nymph.queue;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nymph.lock.NyLock;
/**
 * @说明: 阻塞版的ConcurrentLinkedQueue实现
 * @作者: Nymph
 * @param <E> 队列内的元素类型
 */
public class NyQueue<E> extends ConcurrentLinkedQueue<E> {
	
	private static final Log LOG = LogFactory.getLog(NyQueue.class);

	private static final long serialVersionUID = 1L;

	private final AtomicInteger count = new AtomicInteger();

	private final NyLock nyLock = new NyLock();
	
	/** 往队列尾部插入元素,容量最大值时等待 */
	public boolean offer(E e) {
		try {
			while (size() >= Integer.MAX_VALUE - 1) {
				nyLock.pause();
			}
			boolean offer = super.offer(e);
			if (count.incrementAndGet() - 1 < Integer.MAX_VALUE)
				nyLock.rouse();
			return offer;
		} catch (Exception e1) {
			LOG.error("offer异常", e1);
			return false;
		} 
	}
	/** 当前队列内节点个数 */
	public int size() {
		return count.get();
	}

	/** 从队列头部获取元素, size为空时等待 */
	public E poll() {

		try {
			while(size() == 0) {
				nyLock.pause();
			}
			if (size() >= Integer.MAX_VALUE - 100)
				nyLock.rouse();
			E node = super.poll();
			count.decrementAndGet();
			return node;
		} catch (Exception e) {
			LOG.error("poll异常", e);
			return null;
		} 
	}

}
