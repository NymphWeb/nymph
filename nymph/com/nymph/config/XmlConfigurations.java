package com.nymph.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * xml的配置类
 * @author NYMPH
 * @date 2017年10月7日下午7:12:31
 */
public class XmlConfigurations extends Configuration {
	
	private List<Object> component;
	
	public List<Object> getComponent() {
		return component == null ? Collections.emptyList() : component;
	}

	public void setComponent(List<Object> component) {
		this.component = component;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("XmlConfigurations [webConfig=");
		builder.append(webConfig);
		builder.append(", scanner=");
		builder.append(scanner);
		builder.append(", component=");
		builder.append(component);
		builder.append("]");
		return builder.toString();
	}

	public void addConfiguration(XmlConfigurations configuration) {
		if (component == null) {
			component = new ArrayList<>();
			component.addAll(configuration.getComponent());
		} else {
			component.addAll(configuration.getComponent());
		}
		
		if (scanner == null) {
			scanner = new ArrayList<>();
			scanner.addAll(configuration.getScanner());
		} else {
			scanner.addAll(configuration.getScanner());
		}
		
	}
	
}
