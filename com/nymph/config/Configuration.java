package com.nymph.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Configuration {
	// xml的组件配置
	private List<Object> component;
	// 关于web的配置
	private WebConfig webConfig;
	// 关于包扫描的配置
	private List<String> scanner;
	// 指定bean处理器
	private String beansHandler;
	
	public Optional<String> getBeansHandler() {
		return Optional.ofNullable(beansHandler);
	}

	public void setBeansHandler(String beansHandler) {
		this.beansHandler = beansHandler;
	}

	public WebConfig getWebConfig() {
		return webConfig;
	}

	public void setWebConfig(WebConfig webConfig) {
		this.webConfig = webConfig;
	}

	public List<String> getScanner() {
		return scanner == null ? Collections.emptyList() : scanner;
	}

	public void setScanner(List<String> scanner) {
		this.scanner = scanner;
	}

	public List<Object> getComponent() {
		return component == null ? Collections.emptyList() : component;
	}

	public void setComponent(List<Object> component) {
		this.component = component;
	}
	
	public void addConfiguration(Configuration configuration) {
		if (component == null) {
			component = new ArrayList<>();
			component.addAll(configuration.getComponent());
		} else {
			component.addAll(configuration.getComponent());
		}
	}
	
}
