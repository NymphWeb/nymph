package com.nymph.utils;

import java.util.Date;

public interface PageCSS {

	public static final String TOP = "<br/><h3 style='background:#127392;margin:-0.1%' align='center'><font color='white'>Status.500 Nymph</font></h3><hr/>";

	public static final String DOWN = "<hr/><h3 style='background:#127392;margin:-0.1%' align='center'><font color='white'>Exception</font></h3>";

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
}
