package com.nymph.context.impl;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nymph.context.model.NyParam;
import com.nymph.context.model.NyView;
import com.nymph.context.wrapper.AsyncWrapper;
import com.nymph.queue.NyQueue;
import com.nymph.utils.PoolUtils;

/**
 * 请求调度器,通过三个队列实现并发处理请求和响应结果
 * @author LiuYang
 * @author LiangTianDong
 * @date 2017年9月26日下午8:16:28
 */
public final class NyDispatcher extends HttpServlet {
	private static final long serialVersionUID = 1L;
	/**
	 *  执行3个队列的线程池
	 */
	private final ExecutorService urlPool = PoolUtils.defaultCaheThredPool();
	private final ExecutorService paramPool = PoolUtils.defaultCaheThredPool();
	private final ExecutorService viewPool = PoolUtils.defaultCaheThredPool();
	/**
	 *  asyncContext队列
	 */
	static final NyQueue<AsyncWrapper> ASYNC_QUEUE = new NyQueue<>();
	/**
	 *  请求参数队列
	 */
	static final NyQueue<NyParam> PARAMS_QUEUE = new NyQueue<>();
	/**
	 *  视图队列
	 */
	static final NyQueue<NyView> VIEW_QUEUE = new NyQueue<>();

	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		// 请求开始
		AsyncContext asyncContext = request.startAsync();
		ASYNC_QUEUE.offer(new AsyncWrapper(asyncContext));
	}

	/** 
	 *  执行url解析器
	 */
	public void dispatchUrl() {
		AsyncWrapper context = ASYNC_QUEUE.poll();
		urlPool.execute(new NyUrlResolver(context));
	}

	/** 
	 *  执行参数解析器
	 */
	public void dispatchParam() {
		NyParam param = PARAMS_QUEUE.poll();
		paramPool.execute(new NyParamResolver(param));
	}

	/**
	 *  执行视图解析器
	 */
	public void dispatchView() {
		NyView view = VIEW_QUEUE.poll();
		viewPool.execute(new NyViewResolver(view));
	}

	/** 
	 *  启动队列
	 */
	@Override
	public void init() throws ServletException {
		new Thread(() -> {
			while (true) {dispatchUrl();}
		}).start();

		new Thread(() -> {
			while (true) {dispatchParam();}
		}).start();

		new Thread(() -> {
			while (true) {dispatchView();}
		}).start();
	}

	@Override
	public void destroy() {
		try {
			while ((ASYNC_QUEUE.size() + PARAMS_QUEUE.size() + VIEW_QUEUE.size()) != 0) {
				Thread.sleep(1000);
			}

			urlPool.awaitTermination(30, TimeUnit.MINUTES);
			paramPool.awaitTermination(30, TimeUnit.MINUTES);
			viewPool.awaitTermination(30, TimeUnit.MINUTES);

			urlPool.shutdown();
			paramPool.shutdown();
			viewPool.shutdown();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
