package com.test;

import java.io.Serializable;

public class Man implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String name;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public Man(String name) {
		this.name = name;
	}
	public Man() {
	}
}
