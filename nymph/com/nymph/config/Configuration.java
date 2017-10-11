package com.nymph.config;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public abstract class Configuration {
	
	protected WebConfig webConfig;

	protected List<String> scanner;

	protected String beansHandler;
	
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
	
}
