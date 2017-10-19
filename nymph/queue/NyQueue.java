package com.nymph.queue;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 阻塞版的ConcurrentLinkedQueue实现
 * @author LiuYang
 * @author LiangTianDong
 * @param <E> 队列内的元素类型
 */
public class NyQueue<E> extends ConcurrentLinkedQueue<E> {
	
	private static final long serialVersionUID = 1L;

	private final AtomicInteger size = new AtomicInteger();

	private final NyLock fullLock = new NyLock();

	private final NyLock emptyLock = new NyLock();

	/** 往队列尾部插入元素,容量最大值时等待 */
	public boolean offer(E e) {
		int count = -1;
		try {
			while (size() == Integer.MAX_VALUE) {
				fullLock.pause();
			}
			count = size.getAndIncrement();
			boolean offer = super.offer(e);
			if (count + 1 < Integer.MAX_VALUE) {
				fullLock.rouse();
			}

			if (count == 0) {
				emptyLock.rouse();
			}
			return offer;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	/** 当前队列内节点个数 */
	public int size() {
		return size.get();
	}

	/** 从队列头部获取元素, size为空时等待 */
	public E poll() {
		int count = -1;
		try {
			while(size() == 0) {
				emptyLock.pause();
			}
			count = size.getAndDecrement();
			E node = super.poll();
			if (count > 1) {
				emptyLock.rouse();
			}

			if (count == Integer.MAX_VALUE) {
				fullLock.rouse();
			}
			return node;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
