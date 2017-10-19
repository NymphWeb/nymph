package com.nymph.utils;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class PoolUtils {
	
	public static ThreadPoolExecutor CacheThreadPool(int maxThread) {
		int processors = Runtime.getRuntime().availableProcessors();
		return new ThreadPoolExecutor(processors / 3,
									maxThread,
									60L,
									TimeUnit.SECONDS,
									new LinkedBlockingDeque<>(maxThread));
	}

	public static ThreadPoolExecutor defaultCaheThredPool() {
		int processors = Runtime.getRuntime().availableProcessors();
		return new ThreadPoolExecutor(0,
									processors * 100,
									60L,
									TimeUnit.SECONDS,
									new SynchronousQueue<>());
	}

}
