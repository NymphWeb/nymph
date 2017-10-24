package com.nymph.context.impl;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nymph.context.model.NyParam;
import com.nymph.context.model.NyView;
import com.nymph.context.wrapper.ContextWrapper;
import com.nymph.queue.NyQueue;
import com.nymph.utils.PoolUtil;

/**
 * 请求调度器,通过生产消费的模型来实现并发的处理请求和响应结果
 * @author LiuYang
 * @author LiangTianDong
 * @date 2017年9月26日下午8:16:28
 */
public final class AsyncDispatcher extends HttpServlet implements Runnable {
	private static final long serialVersionUID = 1L;
	// 执行参数队列和视图队列的线程池
	private final ExecutorService paramPool = PoolUtil.cacheThredPool();
	private final ExecutorService viewPool = PoolUtil.cacheThredPool();
	// 参数队列
	private final NyQueue<NyParam> params = new NyQueue<>();
	// 视图队列
	private final NyQueue<NyView> views = new NyQueue<>();

	public void service(HttpServletRequest request, HttpServletResponse response) 
			throws IOException, ServletException {
		// 执行url解析器
		ContextWrapper wrapper = new ContextWrapper(request.startAsync());
		NyUrlResolver urlResolver = new NyUrlResolver(wrapper, params);
		urlResolver.run();
	}
	
	/** 
	 *  执行参数解析器
	 * @throws InterruptedException 
	 */
	public void dispatchParam() throws InterruptedException {
		NyParam param = params.take();
		paramPool.execute(new NyParamResolver(param, views));
	}

	/**
	 *  执行视图解析器
	 * @throws InterruptedException 
	 */
	public void dispatchView() throws InterruptedException {
		NyView view = views.take();
		viewPool.execute(new NyViewResolver(view));
	}

	/** 
	 *  启动队列
	 */
	@Override
	public void init() throws ServletException {
		new Thread(this, "PARAM").start();
		new Thread(this, "VIEW").start();
	}

	@Override
	public void destroy() {
		try {
			paramPool.awaitTermination(30, TimeUnit.MINUTES);
			viewPool.awaitTermination(30, TimeUnit.MINUTES);

			paramPool.shutdown();
			viewPool.shutdown();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		String name = Thread.currentThread().getName();
		
		if ("PARAM".equals(name)) {
			while (true) {
				try {
					dispatchParam();
				} catch (Throwable e) {}
			}
		}
		else {
			while (true) {
				try {
					dispatchView();
				} catch (Throwable e) {}
			}
		}
		
	}
}
