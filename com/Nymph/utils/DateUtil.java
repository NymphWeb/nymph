package com.nymph.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @comment 处理时间类型
 * @author NYMPH
 * @date 2017年9月26日下午8:59:19
 */
public abstract class DateUtil {

	/**
	 * 将Date解析成指定格式的字符串
	 * @param date
	 * @param format
	 * @return
	 */
	public static String resolve(Date date, String format) {
		Instant milli = Instant.ofEpochMilli(date.getTime());
		LocalDateTime ofInstant = LocalDateTime.ofInstant(milli, ZoneId.of("Asia/Shanghai"));
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		return ofInstant.format(formatter);
	}
	/**
	 * 将指定格式的字符串解析成Date
	 * @param date
	 * @param format
	 * @return
	 */
	public static Date resolve(String date, String format) {
		try {
			return new SimpleDateFormat(format).parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new Date();
	}
	/**
	 * 将一个时间格式的字符串解析成Date 只保留年月日
	 * @param date
	 * @return
	 */
	public static Date onlyDate(String date) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd").parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new Date();
	}
	/**
	 * 将一个Date解析成字符串 只保留年月日
	 * @param date
	 * @return
	 */
	public static String onlyDate(Date date) {
		Instant milli = Instant.ofEpochMilli(date.getTime());
		LocalDateTime ofInstant = LocalDateTime.ofInstant(milli, ZoneId.of("Asia/Shanghai"));
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		return ofInstant.format(formatter);
	}
	/**
	 * 将一个Date解析成yyyy-MM-dd HH:mm:ss格式的字符串
	 * @param date
	 * @return
	 */
	public static String dateTime(Date date) {
		Instant milli = Instant.ofEpochMilli(date.getTime());
		LocalDateTime ofInstant = LocalDateTime.ofInstant(milli, ZoneId.of("Asia/Shanghai"));
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		return ofInstant.format(formatter);
	}
	/**
	 * 将一个yyyy-MM-dd HH:mm:ss的字符串解析成Date
	 * @param date
	 * @return
	 */
	public static Date dateTime(String date) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new Date();
	}
}
