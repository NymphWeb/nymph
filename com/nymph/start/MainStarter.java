package com.nymph.start;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.catalina.Context;
import org.apache.catalina.Wrapper;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.ContextConfig;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.startup.Tomcat.FixContextListener;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

import com.nymph.annotation.ConfPosition;
import com.nymph.bean.BeansHandler;
import com.nymph.bean.web.DefaultWebApplicationBeansFactory;
import com.nymph.config.ConfRead;
import com.nymph.utils.BasicUtil;

/**
 * 内嵌tomcat, 用main方法启动这个应用即可
 * @date 2017年9月17日上午2:33:52
 * @author LiuYang
 * @author LiangTianDong
 */
public class MainStarter extends WebApplicationContext {
	// Tomcat实例
	private static final Tomcat TOMCAT = new Tomcat();
	// MainStarter实例
	private static final MainStarter MAIN_STARTER = new MainStarter();
	// 正则的匹配规则
	private static Pattern pattern = Pattern.compile("^nymph.*\\.(xml|yml)$");
	/**
	 * 启动内嵌TOMCAT
	 * @param clazz
	 */
	public static void start(Class<?> clazz) {
		try {
			MAIN_STARTER.load(clazz);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 加载各种配置
	 * @throws Exception
	 */
	private void load(Class<?> clazz) throws Exception {
		// 初始化配置
		ConfPosition conf = clazz.getAnnotation(ConfPosition.class);
		initConfiguration(conf);
		// 初始化bean工厂
		initBeansFactory();

		config = Objects.requireNonNull(configuration.getWebConfig());
		// 设置端口
		TOMCAT.setPort(config.getPort());
		// 设置catalina home
		TOMCAT.setBaseDir(BasicUtil.getSource(""));
		/*
		 * 设置存放视图文件的地方, 如jsp, css, js
		 * 当打成jar包部署时只能填硬盘上存在的目录, 
		 * 因为并不能读取到jar包内的jsp文件
		 */
		Optional<String> webPath = config.getWebappPath();
		String source = BasicUtil.getSource("src/main/webapp");
		String rootPath = webPath.orElse(source);
		File webapp = new File(rootPath);
		
		if (!webapp.exists()) {
			webapp = new File(BasicUtil.getSource("WebContent"));
		}
		if (!webapp.exists()) {
			webapp = new File(BasicUtil.getSource("WebRoot"));
		}

		
		Context context = new StandardContext();
		context.setPath(config.getContextPath());
		context.addLifecycleListener(new FixContextListener());

		if (webapp.exists()) {
			context.addLifecycleListener(new ContextConfig());
			context.setDocBase(webapp.getAbsolutePath());
		}
		// 加载过滤器和servlet
		loadFilter(context);
		loadServlets(context);
		loadEqulasConfig();

		TOMCAT.getHost().addChild(context);
		TOMCAT.start();
		TOMCAT.getServer().await();
	}
	
	/**
	 * 初始化配置
	 * @param conf
	 */
	protected void initConfiguration(ConfPosition conf) {
		if (conf == null) {
			configuration = ConfRead.readConf(defaultConf());
		} else {
			configuration = ConfRead.readConf(conf.value());
		}
	}
	
	/**
	 * 获取classpath下的找到的xml和yml配置文件(文件开头名称为nymph的)
	 * @return	所有找到的文件名字
	 */
	protected String[] defaultConf() {
		File file = new File(BasicUtil.getSource(""));
		List<String> collect = Arrays.stream(file.listFiles()).filter(f -> {
			String name = f.getName();
			Matcher matcher = pattern.matcher(name);
			if (f.isFile() && matcher.matches())
				return true;
			return false;
		}).map(f -> f.getName()).collect(Collectors.toList());
		
		return collect.toArray(new String[collect.size()]);
	}

	/**
	 * 初始化beanFactory
	 */
	protected void initBeansFactory() {
		// 实例化bean处理器
		beansFactory = new DefaultWebApplicationBeansFactory();
		beansFactory.setConfiguration(configuration);
		Optional<String> handler = configuration.getBeansHandler();
		String handlerClass = handler.orElse(DEFAULT_BEANS_HANDLER);
		BeansHandler beansHandler = BasicUtil.newInstance(handlerClass);
		beansFactory.setBeansHandler(beansHandler);
		// 将所有bean对象注册到bean容器
		beansFactory.register();
	}

	/**
	 * 加载一些必须的servlet
	 * @param context tomcat的上下文
	 */
	protected void loadServlets(Context context) {
		Tomcat.initWebappDefaults(context);
		config.getExclutions().forEach(exclude -> {
			context.addServletMappingDecoded(exclude, "default");
		});
		// 核心调度器
		Wrapper dispatcher = Tomcat.addServlet(context, "Nymph", CORE_REQUEST_DISPATCHER);
		dispatcher.setAsyncSupported(true);
		dispatcher.addMapping(config.getUrlPattern());
		dispatcher.setLoadOnStartup(1);
	}

	/**
	 * 加载过滤器
	 * @param context  tomcat的上下文对象
	 */
	protected void loadFilter(Context context) {
		// 编码过滤器
		FilterDef defaultFilter = filterDef(DEFAULT_ENCODING_FILTER);
		defaultFilter.addInitParameter("encoding", config.getEncoding());
		FilterMap defaultMap = filterMap(DEFAULT_ENCODING_FILTER, "/*");
		context.addFilterDef(defaultFilter);
		context.addFilterMap(defaultMap);

		// 自定义的过滤器链
		config.getFilters().forEach(filter -> {
			String urlPattern = "/*";
			if (filter.indexOf("@") > 0) {
				String[] split = filter.split("@");
				urlPattern = split[1];
				filter = split[0];
			}
			FilterDef filterDef = filterDef(filter);
			FilterMap filterMap = filterMap(filter, urlPattern);
			context.addFilterDef(filterDef);
			context.addFilterMap(filterMap);
		});
	}

	/**
	 * 定义一个过滤器
	 * @param className 过滤器的全路径
	 * @return
	 */
	protected FilterDef filterDef(String className) {
		FilterDef def = new FilterDef();
		def.setAsyncSupported("true");
		def.setFilterClass(className);
		def.setFilterName(className);
		return def;
	}

	/**
	 * 设置过滤器的url映射
	 * 
	 * @param name			要映射的过滤器的名字
	 * @param urlPattern	要过滤的url
	 * @return
	 */
	protected FilterMap filterMap(String name, String urlPattern) {
		FilterMap map = new FilterMap();
		map.addURLPattern(urlPattern);
		map.setFilterName(name);
		return map;
	}

}
