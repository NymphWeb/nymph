package com.nymph.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;
/**
 * @comment 配置类, 用于加载配置信息
 * @author NYMPH
 * @date 2017年9月27日上午11:52:25
 */
public class YmlConfigurations extends Configuration {
	
	private DataSource dataSource;
	
	private List<String> beans;
	
	public List<String> getBeans() {
		return beans == null ? Collections.emptyList() : beans;
	}

	public void setBeans(List<String> beans) {
		this.beans = beans;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public DataSource getDataSource() {
		return dataSource;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("YmlConfigurations [dataSource=");
		builder.append(dataSource);
		builder.append(", beans=");
		builder.append(beans);
		builder.append(", webConfig=");
		builder.append(webConfig);
		builder.append(", scanner=");
		builder.append(scanner);
		builder.append("]");
		return builder.toString();
	}

	public void addConfiguration(YmlConfigurations configuration) {
		if (beans == null) {
			beans = new ArrayList<>();
			beans.addAll(configuration.getBeans());
		} else {
			beans.addAll(configuration.getBeans());
		}
		if (scanner == null) {
			scanner = new ArrayList<>();
			scanner.addAll(configuration.getScanner());
		} else {
			scanner.addAll(configuration.getScanner());
		}
	}
}
