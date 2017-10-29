package com.nymph.utils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 常用操作的工具类	
 * @author LiuYang
 * @author LiangTianDong
 * @date 2017年10月2日下午8:34:10
 */
public abstract class BasicUtil {
	
	/**
	 * 根据类路径反射创建对象
	 * 
	 * @param classLocation
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> newInstance(List<String> classLocation) {
		return classLocation.stream().map(ele -> (T)newInstance(ele)).collect(Collectors.toList());
	}
	
	/**
	 * 根据类的全路径创建出一个对象
	 * @param className 包名加类名的字符串
	 * @return			该类的对象
	 */
	@SuppressWarnings("unchecked")
	public static <T> T newInstance(String className) {
		try {
			Class<?> forName = Class.forName(className);
			return (T) forName.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * 根据Class创建出一个实例
	 * @param clazz
	 * @param <T>
	 * @return
	 */
	public static <T> T newInstance(Class<T> clazz) {
		try {
			return clazz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * 获取classpath路径下的资源
	 * @param location
	 * @return
	 */
	public static String getSource(String location) {
		URL url = getDefaultClassLoad().getResource(location.replace(".", "/"));
		return url == null ? "" : url.getPath();
	}
	
	/**
	 * 获取默认的类加载器
	 * @return
	 */
	public static ClassLoader getDefaultClassLoad() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if (loader == null)
			loader = BasicUtil.class.getClassLoader();
		if (loader == null)
			loader = ClassLoader.getSystemClassLoader();
		
		return loader;
	}
	/**
	 * 将字符串转换成和字段对应的类型
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public static Serializable convert(Field field, String value) {
		return convert(field.getType(), value);
	}

	/**
	 * 将字符串转成和给定Class对应的类型
	 * @param clazz
	 * @param value
	 * @return
	 */
	public static Serializable convert(Class<?> clazz, String value) {
		if (String.class == clazz) {
			return value;
		} else if (int.class == clazz || Integer.class == clazz) {
			return Integer.valueOf(value);
		} else if (boolean.class == clazz || Boolean.class == clazz) {
			return Boolean.valueOf(value);
		} else if (double.class == clazz || Double.class == clazz) {
			return Double.valueOf(value);
		} else if (long.class == clazz || Long.class == clazz) {
			return Long.valueOf(value);
		} else if (byte.class == clazz || Byte.class == clazz) {
			return Byte.valueOf(value);
		} else if (float.class == clazz || Float.class == clazz) {
			return Float.valueOf(value);
		} else if (short.class == clazz || Short.class == clazz) {
			return Short.valueOf(value);
		} else if (char.class == clazz || Character.class == clazz) {
			return value.charAt(0);
		} 
		return null;
	}

	/**
	 * 判断一个Class是否是基本类型(包含了String)
	 * @param clazz
	 * @return
	 */
	public static boolean isCommonType(Class<?> clazz) {
		return String.class == clazz ||
				int.class == clazz || Integer.class == clazz ||
				boolean.class == clazz || Boolean.class == clazz || 
				double.class == clazz || Double.class == clazz ||
				long.class == clazz || Long.class == clazz || 
				byte.class == clazz || Byte.class == clazz || 
				float.class == clazz || Float.class == clazz || 
				short.class == clazz || Short.class == clazz || 
				char.class == clazz || Character.class == clazz;
	}

	/**
	 * 判断一个Class是否是集合类型
	 * 
	 * @param clazz
	 * @return
	 */
	public static boolean isCollection(Class<?> clazz) {
		if (Collection.class.isAssignableFrom(clazz))
			return true;
		return false;
	}
	/**
	 * 判断一个Class是否是Map类型
	 * @param clazz
	 * @return
	 */
	public static boolean isMap(Class<?> clazz) {
		if (Map.class.isAssignableFrom(clazz))
			return true;
		return false;
	}

	/**
	 * 判断一个class是否是数组
	 * @param clazz
	 * @return
	 */
	public static boolean isArray(Class<?> clazz) {
		if (clazz.getSimpleName().endsWith("[]"))
			return true;
		return false;
	}

	/**
	 * 判断一个字段是否是基本类型(包含String)
	 * @param field
	 * @return
	 */
	public static boolean isCommonType(Field field) {
		return isCommonType(field.getType());
	}
	/**
	 * 关闭各种连接资源
	 * 
	 * @param closeable
	 */
	public static void closed(AutoCloseable... closeable) {
		for (AutoCloseable autoCloseable : closeable) {
			try {
				if (autoCloseable != null) {
					autoCloseable.close();
				}
			} catch (Exception e) {}
		}
	}

	/**
	 * @备注: 判断集合的泛型是否是普通 类型
	 * @param type
	 * @return
	 */
	public static boolean isCommonCollection(Type type) {
		if (type == null)
			return false;
		Class<?> clazz = (Class<?>) ((ParameterizedType)type).getActualTypeArguments()[0];
		if (Number.class.isAssignableFrom(clazz) || String.class.isAssignableFrom(clazz)) {
			return true;
		}
		return false;
	}

	/**
	 * @备注: 将一个String数组转换为clazz类型的list集合
	 * @param clazz
	 * @param array
	 * @return
	 */
	public static List<?> convertList(Class<?> clazz, String[] array) {
		if (String[].class.equals(clazz)) {
			return Arrays.asList(array).stream().collect(Collectors.toList());
		} else if (Integer[].class.equals(clazz)) {
			return Arrays.asList(array).stream().map(ele -> Integer.valueOf(ele)).collect(Collectors.toList());
		} else if (Long[].class.equals(clazz)) {
			return Arrays.asList(array).stream().map(ele -> Long.valueOf(ele)).collect(Collectors.toList());
		} else if (Boolean[].class.equals(clazz)) {
			return Arrays.asList(array).stream().map(ele -> Boolean.valueOf(ele)).collect(Collectors.toList());
		} else if (Double[].class.equals(clazz)) {
			return Arrays.asList(array).stream().map(ele -> Double.valueOf(ele)).collect(Collectors.toList());
		}
		return Collections.emptyList();
	}
	
	/**
	 * 判断一个数组是否为空和长度是否为0
	 * @param target
	 * @return
	 */
	public static boolean notNullAndLenNotZero(Object[] target) {
		if (target == null || target.length == 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * 如果目标对象为null则返回默认对象
	 * @param target	
	 * @param defaults
	 * @return
	 */
	public static <T> T ifNullDefault(T target, T defaults) {
		if (target == null) {
			return defaults;
		}
		return target;
	}
	
	/**
	 * 如果List为null则返回一个空的List
	 * @param list
	 * @return
	 */
	public static <T>List<T> ofNullList(List<T> list) {
		return list == null ? Collections.emptyList() : list;
	}
	
}
