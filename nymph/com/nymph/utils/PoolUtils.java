package com.nymph.utils;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public interface PoolUtils {
	
	public static ThreadPoolExecutor CacheThreadPool() {
		int processors = Runtime.getRuntime().availableProcessors();
		return new ThreadPoolExecutor(processors / 3, 
									processors * 100, 
									60, 
									TimeUnit.SECONDS,
									new LinkedBlockingDeque<>(processors * 100));
	}

}
