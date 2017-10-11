package com.nymph.utils;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 关于JavaBean的工具类
 * @author NYMPH
 * @date 2017年10月7日下午8:33:21
 */
public abstract class BeanUtils {
	/**
	 * copy符合JavaBean规范的对象
	 * @param source
	 *            源对象
	 * @param target
	 *            要copy的对象
	 * @return
	 */
	public static <T> T copyOf(Object source, T target) {
		return copyOf(source, target, null);
	}

	/**
	 * copy符合JavaBean规范的对象
	 * @param source
	 *            源对象
	 * @param target
	 *            要copy的对象
	 * @param exclude
	 *            要忽略的方法名
	 * @return
	 */
	public static <T> T copyOf(Object source, T target, String exclude) {
		Method[] sourceMethod = source.getClass().getMethods();
		Method[] targetMethod = target.getClass().getMethods();

		Arrays.stream(sourceMethod).forEach(sourceVal -> {
			final String sourceName = sourceVal.getName();
			if (!sourceName.startsWith("get") || sourceName.equals(exclude))
				return;
			Arrays.stream(targetMethod).forEach(targetVal -> {
				String targetName = targetVal.getName();
				if (!targetName.startsWith("set"))
					return;

				targetName = targetName.replace("set", "");
				String replace = sourceName.replace("get", "");

				if (targetName.equals(replace)) {
					try {
						Object invoke = sourceVal.invoke(source);
						if (invoke == null)
							return;
						targetVal.invoke(target, invoke);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		});
		return target;
	}
}
