package com.nymph.bean.impl;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nymph.annotation.Bean;
import com.nymph.annotation.ConfBean;
import com.nymph.bean.BeansComponent;
import com.nymph.bean.BeansHandler;
import com.nymph.bean.HttpBeansContainer;
import com.nymph.config.Configuration;
import com.nymph.config.XmlConfigurations;
import com.nymph.config.YmlConfigurations;
import com.nymph.utils.BasicUtils;
import com.nymph.utils.JarUtils;

/**
 * 根据包路径扫描所有的bean
 * @author NYMPH
 * @date 2017年9月26日2017年9月26日
 */
public class BeanScannerForClasspathAndJar {
	private static final Logger LOG = LoggerFactory.getLogger(BeanScannerForClasspathAndJar.class);
	// 存放bean的容器
	private Map<String, Object> beansContainer;
	// 处理http相关的bean映射信息
	private HttpBeansContainer handler;
	// 处理所有的bean对象
	private BeansHandler beansHandler;
	// bean组件
	private BeansComponent beansComponent;

	/**
	 * 并发的扫描所有bean
	 * @param handler 
	 */
	public void scanner(Configuration configuration) {
		// yml配置文件的处理
		if (configuration instanceof YmlConfigurations) {
			YmlConfigurations ymlConfigurations = (YmlConfigurations) configuration;
			loadBeans(ymlConfigurations.getBeans());
			scanForJarPackages(ymlConfigurations.getScanner());
			scanForClassPathPackages(ymlConfigurations.getScanner());
		}
		
		// xml配置文件的处理
		if (configuration instanceof XmlConfigurations) {
			XmlConfigurations xmlConfigurations = (XmlConfigurations) configuration;
			loadComponent(xmlConfigurations.getComponent());
			scanForJarPackages(xmlConfigurations.getScanner());
			scanForClassPathPackages(xmlConfigurations.getScanner());
		}
	}
	
	public BeanScannerForClasspathAndJar(Map<String, Object> beansContainer, HttpBeansContainer handler, 
			BeansHandler beansHandler, BeansComponent beansComponent) {
		this.handler = handler;
		this.beansContainer = beansContainer;
		this.beansHandler = beansHandler;
		this.beansComponent = beansComponent;
	}
	
	/**
	 * 加载component
	 * @param components
	 */
	public void loadComponent(List<Object> components) {
		components.parallelStream().forEach(component -> {
			Object bean = beansHandler.handlerBefore(component);
			Class<?> clazz = bean.getClass();
			beansComponent.filter(bean);
			beansContainer.putIfAbsent(clazz.getName(), bean);
			beansHandler.handlerAfter(bean);
			LOG.info("component: in container [{}]", clazz);
		});
	}
	
	/**
	 * 加载配置文件中的bean
	 * @param classLocation bean的类路径
	 */
	public void loadBeans(List<String> classLocation) {
		for (String value : classLocation) {
			try {
				Class<?> forName = Class.forName(value);
				interfaceCheck(forName);
				handler.filterAlsoSave(forName);
				// 存入容器之前的处理
				Object bean = beansHandler.handlerBefore(forName.newInstance());
				beansComponent.filter(bean);
				beansContainer.putIfAbsent(forName.getName(), bean);
				// 存入容器之后的处理
				beansHandler.handlerAfter(bean);
				LOG.info("yml: bean in container [{}]", forName);
			} catch (Exception e) {
				LOG.error(null, e);
			}
		}
	}
	
	/**
	 * 扫描classpath下指定包路径的所有类
	 * @param locations	指定的路径, 可以有多个
	 * @throws ClassNotFoundException
	 */
	public void scanForClassPathPackages(List<String> locations) {
		for (String location : locations) {
			String classPath = BasicUtils.getSource(location);
			resolverClasspath(location.replace(".", File.separator), classPath);
		}
	}
	
	/**
	 * 当指定路径存在于jar包中时, 则会扫描这个jar包
	 * @param locations	扫描的目标路径	
	 * @throws ClassNotFoundException 
	 */
	public void scanForJarPackages(List<String> locations) {
		
		for (String location : locations) {
			JarFile jarFile = JarUtils.getJarFile(location);
			if (jarFile == null)
				continue;
			String replace = location.replace(".", File.separator);
			resolveJar(jarFile, replace);
		}
	}

	/**
	 * 在所有的classpath里递归寻找指定的类
	 * @param location	类路径
	 * @param scan		真实的文件路径
	 * @throws ClassNotFoundException
	 */
	public void resolverClasspath(final String scan, String locations) {
		File file = new File(locations);
		if (!file.exists())
			return;
		
		for (File filess : file.listFiles()) {
			if (filess.isDirectory()) {
				resolverClasspath(scan, filess.getPath());
				
			} else if (filess.getName().endsWith(".class")) {
				String paths = filess.getPath().replace(".class", "");
				String path = paths.substring(paths.indexOf(scan));
				String location = path.replace(File.separator, ".");
				try {
					Class<?> forName = Class.forName(location);
					confBeanHandler(forName);
					if (!BasicUtils.existAnno(forName.getAnnotations(), Bean.class) ||
						forName.isInterface()) {
						continue;
					}
					handler.filterAlsoSave(forName);
					// 存入容器之前进行的处理
					Object bean = beansHandler.handlerBefore(forName.newInstance());
					beansComponent.filter(bean);
					beansContainer.putIfAbsent(forName.getName(), bean);
					// 存入容器之后的处理
					beansHandler.handlerAfter(bean);
					
					LOG.info("classpath: bean in container [{}]", forName);
				} catch (Exception e) {
					LOG.error(null, e);
				}
			}
		}
	}
	
	/**
	 * 解析jar包中的类
	 * @param jarFile			表示jar文件
	 * @param packageLocation	扫描的包路径
	 * @throws ClassNotFoundException
	 */
	public void resolveJar(JarFile jarFile, String packageLocation) {
		Enumeration<JarEntry> entries = jarFile.entries();
		while (entries.hasMoreElements()) {
			String classLocation = entries.nextElement().getName();
			// 当jar文件中的路径是以packageLocation指定的路径开头,并且是以.class结尾时
			if (classLocation.startsWith(packageLocation) && 
				classLocation.endsWith(".class")) {
				
				String location = classLocation.replace(".class", "").replace("/", ".");
				try {
					Class<?> forName = Class.forName(location);
					confBeanHandler(forName);
					if (!BasicUtils.existAnno(forName.getAnnotations(), Bean.class) || 
						forName.isInterface()) {
						continue;
					}
					handler.filterAlsoSave(forName);
					// 存入容器之前的处理
					Object bean = beansHandler.handlerBefore(forName.newInstance());
					beansComponent.filter(bean);
					beansContainer.putIfAbsent(forName.getName(), bean);
					// 存入容器之后的处理
					beansHandler.handlerAfter(bean);
					LOG.info("jarFile: bean in container [{}]", forName);
				} catch (Exception e) {
					LOG.error(null, e);
				}
			}
		}
	}
	
	private void confBeanHandler(Class<?> clazz) throws Exception {
		if (clazz.isAnnotationPresent(ConfBean.class)) {
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
