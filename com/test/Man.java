package com.test;

import java.util.Arrays;
import java.util.Properties;

import com.nymph.annotation.Bean;
import com.nymph.annotation.ConfigurationBean;

@ConfigurationBean
public class Man extends Person{
	
	
	private Properties properties;
	
	private String name;
	
	public Properties getProperties() {
		return properties;
	}
	public void setProperties(Properties properties) {
		this.properties = properties;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Bean
	public Man man() {
		Man man = new Man();
		man.setName("黄家驹");
		return man;
	}
	
	public static void main(String[] args) {
		System.out.println(Arrays.toString(Man.class.getDeclaredFields()));
	}
	
}
