package com.nymph.config;

import com.nymph.utils.BasicUtil;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;

/**
 * 加载配置文件
 * @author LiuYang, LiangTianDong
 * @date 2017年9月24日下午4:08:05
 */
public final class YmlRead {

	private static final Class<Configuration> CONFIG_CLASS = Configuration.class;
	
	private static final Yaml yaml = new Yaml();

	/**
	 * 加载yml配置文件
	 * @param locations
	 * @return
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static Configuration readYml(String... locations) {
		Configuration configuration = null;
		ClassLoader classLoad = BasicUtil.getDefaultClassLoad();
		for (String config : locations) {
			InputStream stream = classLoad.getResourceAsStream(config.trim());
			if (configuration == null) {
				configuration = yaml.loadAs(stream, CONFIG_CLASS);
			} else {
				configuration.addConfiguration(yaml.loadAs(stream, CONFIG_CLASS));
			}
		}
		return configuration;
	}

}
