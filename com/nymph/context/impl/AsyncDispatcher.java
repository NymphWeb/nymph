package com.nymph.context.impl;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nymph.context.ContextParameter;
import com.nymph.context.ContextView;
import com.nymph.context.ContextWrapper;
import com.nymph.queue.NyQueue;
import com.nymph.utils.PoolUtil;

/**
 * 	Copyright 2017 author: LiuYang, LiangTianDong
 *
 *	Licensed under the Apache License, Version 2.0 (the "License");
 *	you may not use this file except in compliance with the License.
 *	You may obtain a copy of the License at
 *
 * 		http://www.apache.org/licenses/LICENSE-2.0
 *
 *	Unless required by applicable law or agreed to in writing, software
 *	distributed under the License is distributed on an "AS IS" BASIS,
 *	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *	See the License for the specific language governing permissions and
 *	limitations under the License.
 * 
 * 
 * 请求调度器,通过生产消费的模型来实现并发的处理请求和响应结果
 * @author LiuYang
 * @author LiangTianDong
 * @date 2017年9月26日下午8:16:28
 */
public final class AsyncDispatcher extends HttpServlet implements Runnable {
	private static final long serialVersionUID = 1L;
	// 执行队列的线程池
	private final ExecutorService contextPool = PoolUtil.cacheThredPool();
	private final ExecutorService paramPool = PoolUtil.cacheThredPool();
	private final ExecutorService viewPool = PoolUtil.cacheThredPool();
	// context队列
	private final NyQueue<ContextWrapper> contexts = new NyQueue<>();
	// 参数队列
	private final NyQueue<ContextParameter> params = new NyQueue<>();
	// 视图队列
	private final NyQueue<ContextView> views = new NyQueue<>();

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) 
			throws IOException, ServletException {
		try {
			contexts.put(new ContextWrapper(request.startAsync()));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 执行url解析器
	 * @throws InterruptedException
	 */
	public void dispatchUrl() throws InterruptedException {
		ContextWrapper wrapper = contexts.take();
		contextPool.execute(new ResolverUrlImpl(wrapper, params));
	}
	
	/** 
	 * 执行参数解析器
	 * @throws InterruptedException 
	 */
	public void dispatchParam() throws InterruptedException {
		ContextParameter param = params.take();
		paramPool.execute(new ResovlerParameterImpl(param, views));
	}

	/**
	 * 执行视图解析器
	 * @throws InterruptedException 
	 */
	public void dispatchView() throws InterruptedException {
		ContextView view = views.take();
		viewPool.execute(new ResolverViewImpl(view));
	}

	/** 
	 *  启动队列
	 */
	@Override
	public void init() throws ServletException {
		new Thread(this, "URL").start();
		new Thread(this, "PARAM").start();
		new Thread(this, "VIEW").start();
	}

	@Override
	public void destroy() {
		try {
			contextPool.awaitTermination(30, TimeUnit.MINUTES);
			paramPool.awaitTermination(30, TimeUnit.MINUTES);
			viewPool.awaitTermination(30, TimeUnit.MINUTES);
			
			contextPool.shutdown();
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
		else if ("URL".equals(name)) {
			while (true) {
				try {
					dispatchUrl();
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
