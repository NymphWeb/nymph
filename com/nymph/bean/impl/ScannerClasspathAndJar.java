package com.nymph.bean.impl;

import com.nymph.annotation.Bean;
import com.nymph.annotation.ConfigurationBean;
import com.nymph.bean.BeansHandler;
import com.nymph.bean.component.BeansComponent;
import com.nymph.config.Configuration;
import com.nymph.config.XmlConfigurations;
import com.nymph.config.YmlConfigurations;
import com.nymph.utils.AnnoUtil;
import com.nymph.utils.BasicUtil;
import com.nymph.utils.JarUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 根据包路径扫描所有的bean
 * @author NYMPH
 * @date 2017年9月26日2017年9月26日
 */
public class ScannerClasspathAndJar {
	private static final Logger LOG = LoggerFactory.getLogger(ScannerClasspathAndJar.class);
	// 存放bean的容器
	private Map<String, Object> beansContainer;
	// 处理http相关的bean映射信息
	private HttpBeansContainer handler;
	// 处理所有的bean对象
	private BeansHandler beansHandler;
	// bean组件
	private BeansComponent beansComponent;

	/**
	 * 扫描所有bean
	 */
	public void scanner(Configuration configuration) {
		try {
			// yml配置文件的处理
			if (configuration instanceof YmlConfigurations) {
				YmlConfigurations ymlConfigurations = (YmlConfigurations) configuration;
				loadBeans(ymlConfigurations.getBeans());
			}

			// xml配置文件的处理
			if (configuration instanceof XmlConfigurations) {
				XmlConfigurations xmlConfigurations = (XmlConfigurations) configuration;
				loadComponent(xmlConfigurations.getComponent());
			}
			scanForJarPackages(configuration.getScanner());
			scanForClassPathPackages(configuration.getScanner());
		} catch (Exception e) {
			LOG.error("bean扫描异常", e);
		}

	}
	
	public ScannerClasspathAndJar(Map<String, Object> beansContainer, HttpBeansContainer handler,
								  BeansHandler beansHandler, BeansComponent beansComponent) {
		this.handler = handler;
		this.beansContainer = beansContainer;
		this.beansHandler = beansHandler;
		this.beansComponent = beansComponent;
	}

	/**
	 * 扫描classpath下指定包路径的所有类
	 * @param locations	指定的路径, 可以有多个
	 */
	private void scanForClassPathPackages(List<String> locations) throws Exception {
		for (String location : locations) {
			String classPath = BasicUtil.getSource(location);
			resolverClasspath(location.replace(".", File.separator), classPath);
		}
	}

	/**
	 * 当指定路径存在于jar包中时, 则会扫描这个jar包
	 * @param locations	扫描的目标路径
	 */
	private void scanForJarPackages(List<String> locations) throws Exception {

		for (String location : locations) {
			JarFile jarFile = JarUtil.getJarFile(location);
			if (jarFile == null) continue;
			String replace = location.replace(".", File.separator);
			resolveJar(jarFile, replace);
		}
	}
	
	/**
	 * 加载component
	 * @param components
	 */
	private void loadComponent(List<Object> components) throws IllegalAccessException {
		for (Object component : components) {
			Class<?> aClass = component.getClass();
			beansInitializedHandler(aClass);
		}
	}
	
	/**
	 *
	 * 加载配置文件中的bean
	 * @param classLocation bean的类路径
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 */
	private void loadBeans(List<String> classLocation) throws ClassNotFoundException, IllegalAccessException {
		for (String value : classLocation) {
			Class<?> forName = Class.forName(value);
			interfaceCheck(forName);
			beansInitializedHandler(forName);
		}
	}
	
	/**
	 * 在所有的classpath里递归寻找指定的类
	 * @param scan		递归用,跟locations值相同
	 * @param locations 真实的文件路径
	 */
	private void resolverClasspath(final String scan, String locations) throws Exception {
		File file = new File(locations);
		if (!file.exists())
			return;
		File[] f = file.listFiles();
		if (f == null)
			return;

		for (File files : f) {
			if (files.isDirectory()) {
				resolverClasspath(scan, files.getPath());
			}
			else if (files.getName().endsWith(".class")) {
				String paths = files.getPath().replace(".class", "");
				String path = paths.substring(paths.indexOf(scan));
				String location = path.replace(File.separator, ".");

				Class<?> forName = Class.forName(location);
				configurationBeansHandler(forName);
				Annotation[] annos = forName.getAnnotations();
				if (AnnoUtil.exist(annos, Bean.class) && !forName.isInterface()) {
					beansInitializedHandler(forName);
				}
			}
		}
	}
	
	/**
	 * 解析jar包中的类
	 * @param jarFile			表示jar文件
	 * @param packageLocation	扫描的包路径
	 */
	private void resolveJar(JarFile jarFile, String packageLocation) throws Exception {
		Enumeration<JarEntry> entries = jarFile.entries();
		while (entries.hasMoreElements()) {
			String classLocation = entries.nextElement().getName();
			// 当jar文件中的路径是以packageLocation指定的路径开头,并且是以.class结尾时
			if (classLocation.startsWith(packageLocation) && classLocation.endsWith(".class")) {
				String location = classLocation.replace(".class", "").replace("/", ".");
				Class<?> forName = Class.forName(location);
				configurationBeansHandler(forName);
				Annotation[] annos = forName.getAnnotations();
				if (AnnoUtil.exist(annos, Bean.class) && !forName.isInterface()) {
					beansInitializedHandler(forName);
				}
			}
		}
	}

	/**
	 * 在bean存入容器前后进行一些处理
	 * @param beanClass	bean的类型
	 * @throws IllegalAccessException
	 */
	private void beansInitializedHandler(Class<?> beanClass) throws IllegalAccessException {
		// 存入容器之前的处理
		handler.filterAlsoSave(beanClass);
		Object instance = BasicUtil.newInstance(beanClass);
		Object bean = beansHandler.handlerBefore(instance);
		beansComponent.filter(bean);
		// 存入容器
		beansContainer.putIfAbsent(beanClass.getName(), bean);
		// 存入容器之后的处理
		beansHandler.handlerAfter(bean);
		LOG.info("bean in container [{}]", beanClass);
	}

	/**
	 * 对@ConfBean的处理
	 * @param clazz
	 * @throws Exception
	 */
	private void configurationBeansHandler(Class<?> clazz) throws Exception {
		if (clazz.isAnnotationPresent(ConfigurationBean.class)) {
			Object object = beansContainer.get(clazz.getName());
			if (object == null) {
				object = clazz.newInstance();
			}
			
			for (Method method : clazz.getDeclaredMethods()) {
				if (method.isAnnotationPresent(Bean.class)) {
					Class<?> type = method.getReturnType();
					beansContainer.putIfAbsent(type.getName(), method.invoke(object));
					LOG.info("confBean: in container [{}]", type);
				}
			}
		}
	}

	/**
	 * 实例化yml配置文件里的bean时的检查
	 * @param clazz 类路径创建出来的Class对象
	 */
	private void interfaceCheck(Class<?> clazz) {
		if (clazz.isInterface()) {
			throw new IllegalArgumentException("接口无法实例化");
		}
	}
}
