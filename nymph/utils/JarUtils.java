package com.nymph.utils;

import java.io.IOException;
import java.util.jar.JarFile;

/**
 * 解析Jar包的工具类
 * @author Nymph
 * @date 2017年10月7日下午8:32:40
 */
public abstract class JarUtils {
	
	/**
	 * 根据给出的包路径寻找jar包
	 * @param jarLocation
	 * @return
	 */
	public static JarFile getJarFile(String jarLocation) {
		String location = jarLocation.replace(".", "/");
		String source = BasicUtils.getSource(location);
		String replace = source.replace("file:/", "")
				.replace(String.format("!/%s", location), "");
		
		try {
			return new JarFile(replace);
		} catch (IOException e) {
			return null;
		}
	}
}
