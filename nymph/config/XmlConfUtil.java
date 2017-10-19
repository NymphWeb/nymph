package com.nymph.config;

import java.util.List;
import java.util.stream.Collectors;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.nymph.utils.BasicUtils;

public class XmlConfUtil {
	
	public static XmlConfigurations readXml(String location) {
		try {
			SAXReader sax = new SAXReader();
			Document document = sax.read(
				BasicUtils.getDefaultClassLoad().getResourceAsStream(location));
			Element element = document.getRootElement();
			return read(element);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static XmlConfigurations readXmls(String... locations) {
		try {
			SAXReader sax = new SAXReader();
			boolean isFirst = true;
			XmlConfigurations configurations = null;
			for (String location : locations) {
				Document document = sax.read(
					BasicUtils.getDefaultClassLoad().getResourceAsStream(location));
				Element element = document.getRootElement();
				if (isFirst) {
					configurations = read(element);
				} else {
					configurations.addConfiguration(read(element));
				}
				isFirst = false;
			}
			return configurations;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static XmlConfigurations read(Element element) throws Exception {
		
		XmlConfigurations configuration = new XmlConfigurations();
		
		// 扫描bean的路径
		Element scannerNode = element.element("scanners");
		if (scannerNode != null) {
			List<Element> list = scannerNode.elements();
			List<String> collect2 = list.stream()
				.map(ele -> ele.attribute("location").getValue())
				.collect(Collectors.toList());
			configuration.setScanner(collect2);
		}
		
		// 组件
		List<Object> component = XmlComponent.component(element.elements("component"));
		configuration.setComponent(component);
		
		// bean处理器
		Element handlerNode = element.element("beansHandler");
		configuration.setBeansHandler(attrValue(handlerNode));

		Element webNode = element.element("webConfig");
		if (webNode != null) {
			WebConfig webConfig = new WebConfig();
			// 存放jsp文件的目录
			Element webapp = webNode.element("webapp");
			webConfig.setWebappPath(attrValue(webapp));
			// 端口号
			Element port = webNode.element("port");
			webConfig.setPort(Integer.parseInt(attrValue(port)));
			// 编码
			Element encoding = webNode.element("encoding");
			webConfig.setEncoding(attrValue(encoding));
			// contextPath
			Element contextPath = webNode.element("contextPath");
			webConfig.setContextPath(attrValue(contextPath));
			// 前缀
			Element prefix = webNode.element("prefix");
			webConfig.setPrefix(attrValue(prefix));
			// 后缀
			Element suffix = webNode.element("suffix");
			webConfig.setSuffix(attrValue(suffix));
			// 调度器需要处理的urlPattern
			Element pattern = webNode.element("urlPattern");
			webConfig.setUrlPattern(attrValue(pattern));
			
			// 过滤器
			Element filterNode = webNode.element("filters");
			if (filterNode != null) {
				List<Element> list = filterNode.elements("filter");
				List<String> filters = list.stream().map(ele -> {
					String filter = ele.attribute("class").getValue();
					String urlPattern = ele.attribute("urlPattern").getValue();
					return filter + "@" + urlPattern;
				}).collect(Collectors.toList());
				webConfig.setFilters(filters);
			}
			
			// 拦截器
			Element interceptNode = webNode.element("interceptors");
			if (interceptNode != null) {
				List<Element> list = interceptNode.elements("intercept");
				List<String> interceptors = list.stream()
					.map(ele -> ele.attribute("class").getValue())
					.collect(Collectors.toList());
				webConfig.setInterceptors(interceptors);
			}
			
			// 放行的资源
			Element exclutionNode = webNode.element("exclutions");
			if (exclutionNode != null) {
				List<Element> list = exclutionNode.elements("exclution");
				List<String> exclutions = list.stream()
					.map(ele -> ele.attribute("value").getValue())
					.collect(Collectors.toList());
				webConfig.setExclutions(exclutions);
			}
			
			configuration.setWebConfig(webConfig);
		}
		return configuration;
	}
	
	private static String attrValue(Element element) {
		if (element == null)
			return null;
		return element.attribute(0) == null ? null : element.attribute(0).getValue();
	}
}
