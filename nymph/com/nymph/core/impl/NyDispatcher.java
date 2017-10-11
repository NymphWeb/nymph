package com.nymph.core.impl;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nymph.core.model.NyParam;
import com.nymph.core.model.NyView;
import com.nymph.utils.PoolUtils;

/**
 * 核心调度器,通过三个队列实现并发处理请求和响应结果
 * @author NYMPH
 * @date 2017年9月26日下午8:16:28
 */
public final class NyDispatcher extends HttpServlet {
	private static final long serialVersionUID = 1L;
	/**
	 *  执行3个队列的线程池
	 */
	private static final ExecutorService THREAD_POOL_URL = PoolUtils.CacheThreadPool();
	private static final ExecutorService THREAD_POOL_PARAM = PoolUtils.CacheThreadPool();
	private static final ExecutorService THREAD_POOL_VIEW = PoolUtils.CacheThreadPool();
	/**
	 *  asyncContext队列
	 */
	private static final LinkedBlockingQueue<AsyncRequestWrapper> ASYNC_QUEUE = new LinkedBlockingQueue<>();
	/**
	 *  请求参数队列
	 */
	static final LinkedBlockingQueue<NyParam> PARAMS_QUEUE = new LinkedBlockingQueue<>();
	/**
	 *  视图队列
	 */
	static final LinkedBlockingQueue<NyView> VIEW_QUEUE = new LinkedBlockingQueue<>();

	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 请求开始
		try {
			ASYNC_QUEUE.put(new AsyncRequestWrapper(request.startAsync()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** 
	 *  执行url解析器
	 * @throws InterruptedException 
	 */
	public void dispatchUrl() throws Exception {
		AsyncRequestWrapper context = ASYNC_QUEUE.take();
		THREAD_POOL_URL.execute(new NyUrlResolver(context));
	}

	/** 
	 *  执行参数解析器
	 * @throws InterruptedException 
	 */
	public void dispatchParam() throws Exception {
		NyParam param = PARAMS_QUEUE.take();
		THREAD_POOL_PARAM.execute(new NyParamResolver(param));
	}

	/**
	 *  执行视图解析器
	 * @throws InterruptedException
	 */
	public void dispatchView() throws Exception {
		NyView view = VIEW_QUEUE.take();
		THREAD_POOL_VIEW.execute(new NyViewResolver(view));
	}

	/** 
	 *  启动轮询队列
	 */
	@Override
	public void init() throws ServletException {
		new Thread(() -> {
			while (true) {
				try {
					dispatchUrl();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

		new Thread(() -> {
			while (true) {
				try {
					dispatchParam();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

		new Thread(() -> {
			while (true) {
				try {
					dispatchView();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	@Override
	public void destroy() {
		try {
			while ((ASYNC_QUEUE.size() + PARAMS_QUEUE.size() + VIEW_QUEUE.size()) != 0) {
				Thread.sleep(500);
			}

			THREAD_POOL_URL.awaitTermination(30, TimeUnit.MINUTES);
			THREAD_POOL_PARAM.awaitTermination(30, TimeUnit.MINUTES);
			THREAD_POOL_VIEW.awaitTermination(30, TimeUnit.MINUTES);

			THREAD_POOL_URL.shutdown();
			THREAD_POOL_PARAM.shutdown();
			THREAD_POOL_VIEW.shutdown();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
