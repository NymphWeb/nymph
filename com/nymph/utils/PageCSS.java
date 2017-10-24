package com.nymph.utils;

import java.util.Date;

public interface PageCSS {
	
	public static final String TOP = "<h3 style='background:#127392' align='center'><font color='white'>Nymph Status.500</font></h3><hr/>";

	/**
	 * 用于拼接异常信息
	 */
	public static String join(Throwable throwable) {
		String date = DateUtil.resolve(new Date(), "yyyy-MM-dd HH:mm:ss");
		StringBuffer string = new StringBuffer("[" + date + "]   ");
		string.append("<font color='red'>")
			.append(throwable.toString() + "</font><br/>");
		
		for (StackTraceElement element : throwable.getStackTrace()) {
			string.append("[" + date + "]   ")
				.append("<font color='red'>")
				.append(element + "</font><br/>");
		}
		return string.toString();
	}
}
