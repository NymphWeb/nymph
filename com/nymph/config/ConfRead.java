package com.nymph.config;

/**
 * 加载配置文件(xml或者yml)
 * @author LiuYang, LiangTianDong
 * @date 2017年10月29日下午8:57:19
 */
public class ConfRead {
	
	public static Configuration readConf(String... location) {
		Configuration configuration = null;
		for (String file : location) {
			if (file.endsWith(".xml")) {
				if (configuration == null)
					configuration = XmlRead.readXml(file);
				else
					configuration.addConfiguration(XmlRead.readXml(file));
			} else {
				if (configuration == null)
					configuration = YmlRead.readYml(file);
				else
					configuration.addConfiguration(YmlRead.readYml(file));
			}
		}
		return configuration;
	}
}
