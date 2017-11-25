package com.nymph.json;

import java.io.Serializable;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.nymph.utils.DateUtil;

/**
 * 将普通对象和集合解析成json字符串
 * @author liuYang
 * @author liangTianDong
 */
public abstract class JSONs {

	/**
	 * 将一个对象解析成json字符串
	 * @param object		被转换的对象
	 * @return				json字符串
	 */
	public static String resolve(Object object) {
		return resolve(object, null);
	}

	/**
	 * 将一个对象解析成json字符串, 并且将其中的Date转换成指定的格式
	 * @param object		被转换的对象
	 * @param dateformat	时间格式(yyyy-MM-dd)
	 * @return				json字符串
	 */
	@SuppressWarnings("unchecked")
	public static String resolve(Object object, String dateformat) {
		AtomicInteger integer = new AtomicInteger(0);
		try {
			if (object instanceof String)
				return object.toString();
			if (object instanceof Collection) {
				return collectionToJson((Collection<?>) object, dateformat, integer);
			} else if (object instanceof Map) {
				return mapToJson((Map<String, String>) object);
			} else {
				return commonToJson(object, dateformat, integer);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "null";
		} finally {
			cycleController(integer);
		}
	}

	private static String commonToJson(Object json, String dateformat, AtomicInteger integer) throws Exception {
		if (integer.incrementAndGet() >= 10)
			return null;

		StringBuilder jsons = new StringBuilder("{");
		Field[] fields = json.getClass().getDeclaredFields();
		AccessibleObject.setAccessible(fields, true);

		for (Field field : fields) {
			if (field.isAnnotationPresent(Exclude.class))
				continue;

			String fName = field.getName();

			Name name = null;
			if ((name = field.getAnnotation(Name.class)) != null) {
				fName = name.value();
			}

			jsons.append(",\"").append(fName).append("\":");
			if (null == field.get(json)) {
				jsons.append("\"null\"");
				continue;
			}

			if (isCommonType(field)) {
				jsons.append(field.get(json));

			} else if (Date.class == field.getType()) {
				String resolve = null;
				Date date = (Date) field.get(json);
				if (dateformat != null) {
					resolve = DateUtil.resolve(date, dateformat);
				} else {
					resolve = String.valueOf(date);
				}
				jsons.append("\"" + resolve + "\"");

			} else if (String.class == field.getType() || isBigRange(field)) {
				jsons.append("\"" + field.get(json) + "\"");

			} else if (Collection.class.isAssignableFrom(field.getType())) {
				jsons.append(collectionToJson((Collection<?>) field.get(json), dateformat, integer));

			} else {
				jsons.append(commonToJson(field.get(json), dateformat, integer));
			}
		}
		return jsons.delete(1, 2).append("}").toString();
	}

	private static String collectionToJson(Collection<?> coll, String dateformat, AtomicInteger integer) throws Exception {
		if (integer.incrementAndGet() >= 10)
			return null;

		StringBuilder jsons = new StringBuilder("[");
		for (Object object : coll) {
			jsons.append(",").append(commonToJson(object, dateformat, integer));
		}
		return jsons.delete(1, 2).append("]").toString();
	}

	private static String mapToJson(Map<String, String> map) {
		return map.toString().replace("=", "\":\"").replace(", ", "\", \"").replace("{", "{\"").replace("}", "\"}");
	}

	private static boolean isCommonType(Field field) {
		Class<?> clazz = field.getType();

		if (int.class == clazz || Integer.class == clazz || boolean.class == clazz || Boolean.class == clazz
				|| double.class == clazz || Double.class == clazz || long.class == clazz || Long.class == clazz
				|| byte.class == clazz || Byte.class == clazz || float.class == clazz || Float.class == clazz
				|| short.class == clazz || Short.class == clazz || char.class == clazz || Character.class == clazz
				|| Number.class == clazz) {

			return true;
		}
		return false;
	}

	private static boolean isBigRange(Field field) {
		Class<?> clazz = field.getType();
		return Object.class == clazz || Serializable.class == clazz;
	}

	private static void cycleController(AtomicInteger integer) {
		if (integer.get() >= 10)
			throw new IllegalArgumentException("请检查被解析的类是否符合Javabean规范, 或者某个字段是否嵌套过多");
	}

}
