package com.nymph.lock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
/**
 * 控制核心调度器的三个容器正常运行的锁
 * @author NYmph
 *
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
public class NyLock {
	
	private static final Log LOG = LogFactory.getLog(NyLock.class);
	private final ReentrantLock lock= new ReentrantLock();
	private final Condition condition = lock.newCondition();
	/** 让当前线程等待 */
	public void pause(){
		lock.lock();
		try {
			condition.await();
		} catch (Exception e) {
			LOG.error("线程等待异常", e);
		} finally{
			lock.unlock();
		}
	}
	/** 唤醒一个持有lock的线程 */
	public  void rouse(){
		lock.lock();
		try {
			condition.signal();
		} catch (Exception e) {
			LOG.error("线程唤醒异常", e);
		}finally{
			lock.unlock();
		}
	}
	/** 唤醒所有持有lock的线程 */
	public  void rouseAll(){
		lock.lock();
		try {
			condition.signalAll();
		} catch (Exception e) {
			LOG.error("唤醒所有线程异常", e);
		}finally{
			lock.unlock();
		}
	}


}
