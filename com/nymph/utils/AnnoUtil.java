package com.nymph.utils;

import java.lang.annotation.Annotation;
/**
 * 关于注解的工具类
 * @author LiuYang
 * @author LiangTianDong
 * @date 2017年10月21日下午5:21:10
 */
public abstract class AnnoUtil {
	
	/**
	 * 判断一个注解数组是否包含某个注解相同类型的注解...
	 * @param annos  注解数组
	 * @param parent 目标注解
	 * @return 注解数组中包含的注解
	 */
	public static Annotation get(Annotation[] annos, Class<? extends Annotation> parent) {
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
	public static Class<? extends Annotation> getType(Annotation[] annos, Class<? extends Annotation> parent) {
		for (Annotation annotation : annos) {
			Class<? extends Annotation> annotationType = annotation.annotationType();
			if (annotationType.isAnnotationPresent(parent)) {
				return annotationType;
			}
		}
		return null;
	}
	/**
	 * 判断一个注解数组中是否包含某个注解
	 * @param annos		注解数组
	 * @param parent	目标注解
	 * @return <code>true</code> <b>or</b> <code>false</code>
	 */
	public static boolean exist(Annotation[] annos, Class<? extends Annotation> parent) {
		for (Annotation annotation : annos) {
			if (annotation.annotationType().isAnnotationPresent(parent) || 
				annotation.annotationType() == parent) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 获取注解的指定方法的值
	 * @param annotation 目标注解
	 * @param methodName 注解的方法名
	 * @return			   结果
	 */
	public static Object invoke(Annotation annotation, String methodName) {
		try {
			Class<? extends Annotation> annotationType = annotation.annotationType();
			return annotationType.getMethod(methodName).invoke(annotation);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 获取注解的value方法的值
	 * @param annotation 目标注解
	 * @return			   结果
	 */
	public static String getValueOfValueMethod(Annotation annotation) {
		return String.valueOf(invoke(annotation, "value"));
	}

}
