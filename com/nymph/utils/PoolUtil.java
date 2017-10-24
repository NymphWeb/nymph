package com.nymph.utils;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class PoolUtil {
	
	public static ThreadPoolExecutor fixedThreadPool() {
		int processors = Runtime.getRuntime().availableProcessors();
		return new ThreadPoolExecutor(processors * 2,
									processors * 2,
									0L,
									TimeUnit.MILLISECONDS,
									new LinkedBlockingQueue<>());
	}

	public static ThreadPoolExecutor cacheThredPool() {
		int processors = Runtime.getRuntime().availableProcessors();
		return new ThreadPoolExecutor(0,
									processors * 100,
									60L,
									TimeUnit.SECONDS,
									new SynchronousQueue<>());
	}

}
