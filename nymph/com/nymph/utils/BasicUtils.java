package com.nymph.utils;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 常用操作的工具类	
 * @author NYMPH
 * @date 2017年10月2日下午8:34:10
 */
public abstract class BasicUtils {
	
	/**
	 * 根据类路径反射创建对象
	 * 
	 * @param classLocation
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> newInstance(List<String> classLocation) {
		return classLocation.parallelStream().map(ele -> {
			try {
				Class<?> forName = Class.forName(ele);
				return (T) forName.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}).collect(Collectors.toList());
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
			e.printStackTrace();
			return null;
		}
	}

	
	public static String getSource(String location) {
		URL url = getDefaultClassLoad().getResource(location.replace(".", "/"));
		return url == null ? "" : url.getPath();
	}
	
	
	public static ClassLoader getDefaultClassLoad() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if (loader == null)
			loader = BasicUtils.class.getClassLoader();
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
		throw new IllegalArgumentException("不是基本类型或String");
	}

	/**
	 * 判断一个Class是否是基本类型(包含了String)
	 * @param clazz
	 * @return
	 */
	public static boolean isCommonType(Class<?> clazz) {
		String simpleName = clazz.getSimpleName().toUpperCase();
		switch (simpleName) {
		case "STRING":
		case "INT":
		case "INTEGER":
		case "BOOLEAN":
		case "NUMBER":
		case "SHORT":
		case "BYTE":
		case "LONG":
		case "DOUBLE":
		case "FLOAT":
		case "CHAR":
		case "CHARACTER":
			return true;
		default:
			return false;
		}
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
	 * 根据类型返回对应的集合
	 * @param clazz
	 * @param params
	 * @param type
	 * @return
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static List<?> resultType(Class<?> clazz, Map<String, String[]> params, ParameterizedType type, String format) 
			throws InstantiationException, IllegalAccessException {
		
		List<Object> objs = new ArrayList<>();
		Class<?> generic = (Class<?>) type.getActualTypeArguments()[0];
		Field[] fields = generic.getDeclaredFields();
		int length = 0;
		for (Field field : fields) {
			String[] strings = params.get(field.getName());
			if (strings == null) continue;
			
			length = length < strings.length ? strings.length : length;
		}

		for (int i = 0; i < length; i++) {
			objs.add(resultType(generic, params, i, format));
		}
		return objs;
	}

	/**
	 * 根据类型将map中的值封装成一个对象并返回
	 * @param clazz
	 * @param params
	 * @param index
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static Object resultType(Class<?> clazz, Map<String, String[]> params, int index, String format) 
			throws InstantiationException, IllegalAccessException {
		
		Object instance = clazz.newInstance();
		Field[] fields = clazz.getDeclaredFields();
		AccessibleObject.setAccessible(fields, true);
		for (Field field : fields) {
			if (isCollection(clazz)) {
				ParameterizedType paramType = (ParameterizedType) field.getGenericType();
				instance = resultType(clazz, params, paramType, format);
				continue;
			} 
			
			String[] vals = params.get(field.getName());
			if (vals == null || index >= vals.length)
				continue;
			
			String param = vals[index];
			
			if (isCommonType(field)) {
				field.set(instance, convert(field, param));
				
			} else if (field.getType() == Date.class) {
				Date date = DateUtils.resolve(param, format);
				field.set(instance, date);
				
			} else {
				resultType(field.getType(), params, 0, format);
			}
		}
		return instance;
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
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 主要用于拼接异常信息
	 */
	public static String join(Object... array) {
		StringBuffer string = new StringBuffer();
		String date = DateUtils.resolve(new Date(), "yyyy-MM-dd HH:mm:ss");
		for (Object element : array) {
			if (element instanceof Object[]) {
				for (Object t : (Object[]) element) {
					string.append("<font color='green'>[" + date + "]</font>  ")
					    .append("<font color='red'>")
					    .append(t + "</font><br/>");
				}
				continue;
			}
			string.append("<font color='green'>[" + date + "]</font>  ")
				.append("<font color='red'>")
				.append(element + "</font><br/>");
		}
		return string.toString();
	}

	/**
	 * @备注: 判断集合的泛型是否是普通 类型
	 * @param type
	 * @return
	 */
	public static boolean isCommonCollection(Type type) {
		if (type == null)
			return false;
		Class<?> clazz = (Class<?>) (((ParameterizedType)type).getActualTypeArguments()[0]);
		if (Number.class.isAssignableFrom(clazz) || 
			String.class.isAssignableFrom(clazz)) {
			
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
			return Arrays.asList(array).stream().map(ele -> ele).collect(Collectors.toList());
		} else if (Integer[].class.equals(clazz)) {
			return Arrays.asList(array).stream().map(ele -> Integer.valueOf(ele)).collect(Collectors.toList());
		} else if (Long[].class.equals(clazz)) {
			return Arrays.asList(array).stream().map(ele -> Long.valueOf(ele)).collect(Collectors.toList());
		} else if (Boolean[].class.equals(clazz)) {
			return Arrays.asList(array).stream().map(ele -> Boolean.valueOf(ele)).collect(Collectors.toList());
		} else if (Double[].class.equals(clazz)) {
			return Arrays.asList(array).stream().map(ele -> Double.valueOf(ele)).collect(Collectors.toList());
		}
		throw new IllegalArgumentException("非法的参数！！！");
	}

	/**
	 * 将String数组转成和Class相同的类型的数组, {应该用List替代数组}
	 * 
	 * @param clazz
	 * @param array
	 * @return
	 */
	@Deprecated
	public static Object[] convertArray(Class<?> clazz, String[] array) {
		if (String[].class.equals(clazz)) {
			return array;
			
		} else if (Integer[].class.equals(clazz) || "int[]".equals(clazz.getSimpleName())) {
			List<Integer> list = Arrays.asList(array).stream().map(ele -> Integer.valueOf(ele))
					.collect(Collectors.toList());
			return list.toArray(new Integer[array.length]);
			
		} else if (Long[].class.equals(clazz) || "long[]".equals(clazz.getSimpleName())) {
			List<Long> list = Arrays.asList(array).stream().map(ele -> Long.valueOf(ele))
					.collect(Collectors.toList());
			return list.toArray(new Long[array.length]);
			
		} else if (Boolean[].class.equals(clazz) || "boolean[]".equals(clazz.getSimpleName())) {
			List<Boolean> list = Arrays.asList(array).stream().map(ele -> Boolean.valueOf(ele))
					.collect(Collectors.toList());
			return list.toArray(new Boolean[array.length]);
			
		} else if (Double[].class.equals(clazz) || "double[]".equals(clazz.getSimpleName())) {
			List<Double> list = Arrays.asList(array).stream().map(ele -> Double.valueOf(ele))
					.collect(Collectors.toList());
			return list.toArray(new Double[array.length]);
		}
		throw new IllegalArgumentException("非法的参数！！！");
	}

	/**
	 * 获取字段的泛型
	 * 
	 * @param field
	 * @param index 泛型的位置
	 * @return 泛型类型
	 */
	public static Class<?> genericType(Field field, int index) {
		ParameterizedType param = (ParameterizedType) field.getGenericType();
		return param.getActualTypeArguments()[index].getClass();
	}

	/**
	 * 在一串Integer中寻找最大的数
	 * 
	 * @param num
	 * @return max value
	 */
	public static Integer max(Integer... num) {
		Optional<Integer> max = Arrays.asList(num).parallelStream().max((o1, o2) -> o1 - o2);
		return max.orElse(0);
	}

	/**
	 * 判断一个注解数组是否包含某个注解相同类型的注解...
	 * 
	 * @param annos
	 *            注解数组
	 * @param parent
	 *            目标注解
	 * @return 注解数组中包含的注解
	 */
	public static Annotation getAnno(Annotation[] annos, Class<? extends Annotation> parent) {
		for (Annotation annotation : annos) {
			if (annotation.annotationType().isAnnotationPresent(parent)) {
				return annotation;
			}
		}
		return null;
	}
	
	/**
	 * 判断一个注解数组是否包含某个注解相同类型的注解...
	 * @param annos  注解数组
	 * @param parent 目标注解
	 * @return 注解数组中包含的注解类型
	 */
	public static Class<? extends Annotation> getAnnoType(Annotation[] annos, Class<? extends Annotation> parent) {
		for (Annotation annotation : annos) {
			Class<? extends Annotation> annotationType = annotation.annotationType();
			if (annotationType.isAnnotationPresent(parent)) {
				return annotationType;
			}
		}
		return null;
	}
	/**
	 * 判断一个注解集合中是否包含某个注解
	 * @comment 
	 * @param annos
	 * @param parent
	 * @return <code>true</code> <b>or</b> <code>false</code>
	 */
	public static boolean existAnno(Annotation[] annos, Class<? extends Annotation> parent) {
		for (Annotation annotation : annos) {
			if (annotation.annotationType().isAnnotationPresent(parent) || 
				annotation.annotationType() == parent) {
				return true;
			}
		}
		return false;
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
