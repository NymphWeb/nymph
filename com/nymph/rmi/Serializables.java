package com.nymph.rmi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

/**
 * 序列化工具类
 * @author LiuYang, LiangTianDong
 * @date 2017年10月31日下午11:11:28
 */
public class Serializables {
	
	/**
	 * 将一个对象序列化成一个byte数组
	 * @param object
	 * @return
	 */
	public static byte[] serialize(Object object) {
		ByteArrayOutputStream baos = null;
		ObjectOutputStream oos = null;
		try {
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			return baos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			clear(oos, baos);
		}
	}
	
	/**
	 * 将一个byte数组反序列化成对象
	 * @param bytes
	 * @return
	 */
	public static Object deSerialize(byte[] bytes) {
		return deSerialize(bytes, 0, bytes.length);
	}
	
	/**
	 * 将一个byte数组反序列化成对象
	 * @param bytes		字节数组
	 * @param position	开始位置
	 * @param length	长度
	 * @return
	 */
	public static Object deSerialize(byte[] bytes, int position, int length) {
		ByteArrayInputStream bais = null;
		ObjectInputStream ois = null;
		try {
			bais = new ByteArrayInputStream(bytes, position, length);
			ois = new ObjectInputStream(bais);
			return ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			clear(ois, bais);
		}
	}
	
	/**
	 * 关闭资源
	 * @param closeable
	 */
	public static void clear(AutoCloseable... closeable) {
		Arrays.stream(closeable).forEach(close -> {
			if (null != close) {
				try {
					close.close();
				} catch (Exception e) {}
			}
		});
	}

}
