package com.nymph.queue;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ConcurrentLinkedQueue和LinkedBlockingQueue的结合...
 * @author LiuYang
 * @author LiangTianDong
 * @param <E> 队列内的元素类型
 */
public class NyQueue<E> extends ConcurrentLinkedQueue<E> {
	
	private static final long serialVersionUID = 1L;

	private final AtomicInteger size = new AtomicInteger();

	/** Lock held by take, poll, etc */
    private final ReentrantLock takeLock = new ReentrantLock();

    /** Wait queue for waiting takes */
    private final Condition notEmpty = takeLock.newCondition();

    /** Lock held by put, offer, etc */
    private final ReentrantLock putLock = new ReentrantLock();

    /** Wait queue for waiting puts */
    private final Condition notFull = putLock.newCondition();

	/** 
	 * 往队列尾部插入元素,容量最大值时等待 
	 * @return 
	 * @throws InterruptedException 
	 */
	public void put(E e) throws InterruptedException {
        int c = -1;
        final ReentrantLock putLock = this.putLock;
        final AtomicInteger count = this.size;
        putLock.lockInterruptibly();
        try {
            /*
             * Note that count is used in wait guard even though it is
             * not protected by lock. This works because count can
             * only decrease at this point (all other puts are shut
             * out by lock), and we (or some other waiting put) are
             * signalled if it ever changes from capacity. Similarly
             * for all other uses of count in other wait guards.
             */
            while (count.get() == Integer.MAX_VALUE) {
                notFull.await();
            }
            super.offer(e);
            c = count.getAndIncrement();
            if (c + 1 < Integer.MAX_VALUE)
                notFull.signal();
        } finally {
            putLock.unlock();
        }
        if (c == 0)
            signalNotEmpty();
	}
	/** 当前队列内节点个数 */
	public int size() {
		return size.get();
	}

	/** 从队列头部获取元素, size为空时等待 
	 * @throws InterruptedException */
	public E take() throws InterruptedException {
		E x;
        int c = -1;
        final AtomicInteger count = this.size;
        final ReentrantLock takeLock = this.takeLock;
        takeLock.lockInterruptibly();
        try {
            while (count.get() == 0) {
                notEmpty.await();
            }
            x = super.poll();
            c = count.getAndDecrement();
            if (c > 1)
                notEmpty.signal();
        } finally {
            takeLock.unlock();
        }
        if (c == Integer.MAX_VALUE)
            signalNotFull();
        return x;
	}
	
	/**
     * Signals a waiting take. Called only from put/offer (which do not
     * otherwise ordinarily lock takeLock.)
     */
	private void signalNotEmpty() {
        final ReentrantLock takeLock = this.takeLock;
        takeLock.lock();
        try {
            notEmpty.signal();
        } finally {
            takeLock.unlock();
        }
    }
	
	/**
     * Signals a waiting put. Called only from take/poll.
     */
    private void signalNotFull() {
        final ReentrantLock putLock = this.putLock;
        putLock.lock();
        try {
            notFull.signal();
        } finally {
            putLock.unlock();
        }
    }

}
