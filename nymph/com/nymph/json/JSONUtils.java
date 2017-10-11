package com.nymph.json;

import java.io.Serializable;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import com.nymph.utils.DateUtils;

/**
 * 将普通对象和集合解析成json字符串
 * 
 * @author liuYang
 * @author liangTianDong
 */
public abstract class JSONUtils {

	private static String commonToJson(Object json, String Dateformat, int count) throws Exception {
		if (count >= 5) {
			throw new IllegalArgumentException("请检查被解析的类是否符合Javabean规范, 或者某个字段是否嵌套过多");
		}
		count++;

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
				if (Dateformat != null) {
					resolve = DateUtils.resolve(date, Dateformat);
				} else {
					resolve = String.valueOf(date);
				}
				jsons.append("\"" + resolve + "\"");

			} else if (String.class == field.getType() || isBigRange(field)) {
				jsons.append("\"" + field.get(json) + "\"");

			} else if (Collection.class.isAssignableFrom(field.getType())) {
				jsons.append(collectionToJson((Collection<?>) field.get(json), Dateformat, count));

			} else {
				jsons.append(commonToJson(field.get(json), Dateformat, count));
			}
		}
		return jsons.delete(1, 2).append("}").toString();
	}

	public static String collectionToJson(Collection<?> coll, String Dateformat, int count) throws Exception {
		if (count >= 5) {
			throw new IllegalArgumentException("请检查被解析的类是否符合Javabean规范, 或者某个字段是否嵌套过多");
		}
		count++;

		StringBuilder jsons = new StringBuilder("[");
		for (Object object : coll) {
			jsons.append(",").append(commonToJson(object, Dateformat, count));
		}
		return jsons.delete(1, 2).append("]").toString();
	}

	public static String mapToJson(Map<String, String> map) {
		return map.toString().replace("=", "\":\"").replace(", ", "\", \"").replace("{", "{\"").replace("}", "\"}");
	}

	@SuppressWarnings("unchecked")
	public static String resolve(Object object, String Dateformat) {
		int count = 0;
		try {
			if (object instanceof String)
				return object.toString();
			if (object instanceof Collection) {
				return collectionToJson((Collection<?>) object, Dateformat, count);
			} else if (object instanceof Map) {
				return mapToJson((Map<String, String>) object);
			} else {
				return commonToJson(object, Dateformat, count);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "null";
		}
	}

	@SuppressWarnings("unchecked")
	public static String resolve(Object object) {
		int count = 0;
		
		try {
			if (object instanceof String)
				return object.toString();
			if (object instanceof Collection) {
				return collectionToJson((Collection<?>) object, null, count);
			} else if (object instanceof Map) {
				return mapToJson((Map<String, String>) object);
			} else {
				return commonToJson(object, null, count);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "null";
		}

	}

	public static boolean isCommonType(Field field) {
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

	public static boolean isBigRange(Field field) {
		Class<?> clazz = field.getType();
		return Object.class == clazz || Serializable.class == clazz;
	}

}
