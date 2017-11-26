package com.nymph.start;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRegistration.Dynamic;

import com.nymph.bean.BeansHandler;
import com.nymph.config.ConfRead;
import com.nymph.context.WebApplicationInitialization;
import com.nymph.utils.BasicUtil;

/**
 * webxml的启动方式	
 * @author NYMPH
 * @date 2017年10月11日上午11:48:29
 */
public class WebXmlStarter extends WebApplicationInitialization implements ServletContextListener {

	/**
	 *  context param 中的name的名称, web.xml方式时使用的配置,用于指定yml文件的名称
	 */
	private static final String CONFIGURATIONS_PARAM_NAME = "configurations";
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		// 加载配置文件
		ServletContext context = sce.getServletContext();
		loadConfig(context.getInitParameter(CONFIGURATIONS_PARAM_NAME));
		
		// 实例化bean处理器
		Optional<String> handler = getConfiguration().getBeansHandler();
		String finalClass = handler.orElse(DEFAULT_BEANS_HANDLER);
		BeansHandler beansHandler = BasicUtil.newInstance(finalClass);
		getBeansFactory().setBeansHandler(beansHandler);
		getBeansFactory().setConfiguration(getConfiguration());
		// 将所有bean对象注册到bean容器
		getBeansFactory().register();
		loadServlets(context);
	}
	
	/**
	 * 加载必须的servlet
	 * @param context	servletContext对象
	 */
	public void loadServlets(ServletContext context) {
		config = getConfiguration().getWebConfig();
		// 加载编码过滤器
		javax.servlet.FilterRegistration.Dynamic filter = 
				context.addFilter("ENCODING", DEFAULT_ENCODING_FILTER);
		filter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
		filter.setAsyncSupported(true);
		filter.setInitParameter("encoding", config.getEncoding());

		// 放行静态资源的配置
		List<String> source = config.getExclutions();
		ServletRegistration defaultServlet = context.getServletRegistration("default");
		defaultServlet.addMapping(source.toArray(new String[source.size()]));
		
		// 加载NyDispatcher
		Dynamic servlet = context.addServlet("Nymph", CORE_REQUEST_DISPATCHER);
		servlet.addMapping(config.getUrlPattern());
		servlet.setAsyncSupported(true);

		try {
			loadCustomFilter(context, config.getFilters());
			initBaseConfig();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 动态的加载过滤器, 不需要再webxml中配置, 只需要在yml文件中配置全路径即可, 配置多个过滤器时可以形成过滤器链
	 * @param context	ServletContext servlet上下文
	 * @param filters	过滤器的全路径, 可以配置多个
	 */
	private void loadCustomFilter(ServletContext context, List<String> filters) {
		filters.forEach(filter -> {
			String urlPattern = "/*";
			
			if (filter.indexOf("@") > 0) {
				String[] split = filter.split("@");
				urlPattern = split[1];
				filter = split[0];
			}
			javax.servlet.FilterRegistration.Dynamic dynamic =
					context.addFilter(filter, filter);

			dynamic.setAsyncSupported(true); // 设置过滤器的异步支持
			dynamic.addMappingForUrlPatterns(
					EnumSet.of(DispatcherType.REQUEST), true, urlPattern);
		});
	}
	
	/**
	 * 加载yml配置文件
	 * @param location yml配置文件的位置
	 */
	private void loadConfig(String location) {
		if (location.indexOf(",") < 0)
			setConfiguration(ConfRead.readConf(location));
		else
			setConfiguration(ConfRead.readConf(location.split(",")));
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {}

}
