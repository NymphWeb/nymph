package com.nymph.utils;

/**
 * 字符串的工具类
 * @author LiuYang
 * @author LiangTianDong
 * @date 2017年10月1日下午9:57:36
 */
public interface StrUtils {
	
	/**
	 * 取出字符串中与 指定的不相同的字符串 
	 * @param source
	 * @param str
	 * @return
	 */
	public static String contrary(String source, String remove) {
		for (char chars : remove.toCharArray()) {
			source = source.replace(chars + "", "");
		}
		return source;
	}
	
	/**
	 * 删除字符串中指定子串
	 * @param source
	 * @param delete
	 * @return 
	 */
	public static String delete(String source, String delete) {
		return source.replace(delete, "");
	}
	
	/**
	 * 通过正则表达式来删除掉匹配的字符
	 * @param source
	 * @param regex
	 * @return
	 */
	public static String remove(String source, String regex) {
		return source.replaceAll(regex, "");
	}
	
	/**
	 * 如果传入的字符串为null则返回一个空字符, 避免出现空指针
	 * @param string
	 * @return
	 */
	public static String notNull(String string) {
		return string == null ? "" : string;
	}
	/**
	 * 截取一个字符串, 从start开始end结束, 结果不包含start和end
	 * @param string
	 * @param start
	 * @param end
	 * @return
	 */
	public static String midVal(String string, String start, String end) {
		int offset = start.length();
		int indexOf = string.indexOf(start);
		int lastIndexOf = string.lastIndexOf(end);
		return string.substring(indexOf + offset, lastIndexOf);
	}
	/**
	 * 截取一个字符串, 从start开始end结束, 结果包含start和end
	 * @param string
	 * @param start
	 * @param end
	 * @return
	 */
	public static String midValc(String string, String start, String end) {
		int indexOf = string.indexOf(start);
		int lastIndexOf = string.lastIndexOf(end);
		int offset = end.length();
		return string.substring(indexOf, lastIndexOf + offset);
	}
	/**
	 * 比较两个字符串中是否有相同数量的字符
	 * @param str1
	 * @param str2
	 * @param c
	 * @return
	 */
	public static boolean equalsNum(String str1, String str2, char c) {
		int count1 = 0, count2 = 0;
		
		for (char ch1 : str1.toCharArray()) {
			if (ch1 == c)
				count1++;
		}
		for (char ch2 : str2.toCharArray()) {
			if (ch2 == c)
				count2++;
		}
		
		return count1 == count2;
	}
	
	/**
	 * 将字符串的首字母大写
	 * @param source
	 * @return
	 */
	public static String firstUpper(String source) {
		return source.replaceFirst("\\w", String.valueOf(source.charAt(0)).toUpperCase());
	}
}
