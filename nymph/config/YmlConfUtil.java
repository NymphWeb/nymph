package com.nymph.config;

import com.nymph.utils.BasicUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;

/**
 * @说明 加载配置文件
 * @作者 NYMPH
 * @创建时间 2017年9月24日下午4:08:05
 */
public final class YmlConfUtil {

	private static final Class<YmlConfigurations> CONFIG_CLASS = YmlConfigurations.class;

	/**
	 * 加载一个yml配置文件
	 * 
	 * @param location
	 * @return
	 */
	public static YmlConfigurations readYml(String location) {
		Yaml yaml = new Yaml();
		InputStream stream = BasicUtils.getDefaultClassLoad().getResourceAsStream(location);
		return yaml.loadAs(stream, CONFIG_CLASS);
	}

	/**
	 * 加载多个yml配置文件
	 * @param locations
	 * @return
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static YmlConfigurations readYmls(String... locations) {
		Yaml yaml = new Yaml();
		YmlConfigurations ymlConfigurations = null;
		ClassLoader classLoad = BasicUtils.getDefaultClassLoad();
		boolean isFirst = true;
		for (String config : locations) {
			InputStream stream = classLoad.getResourceAsStream(config.trim());
			if (isFirst) {
				ymlConfigurations = yaml.loadAs(stream, CONFIG_CLASS);
			} else {
				ymlConfigurations.addConfiguration(yaml.loadAs(stream, CONFIG_CLASS));
			}
			isFirst = false;
		}
		return ymlConfigurations;
	}

}
