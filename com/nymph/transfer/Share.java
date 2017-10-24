package com.nymph.transfer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.nymph.context.wrapper.ContextWrapper;
/**
 * 文件下载的相关类
 * @author liu yang 
 * @author liang tian dong
 * @date 2017年9月29日下午3:34:32
 */
public class Share {

	private final ContextWrapper asyncReq;
	
	public Share(ContextWrapper asyncReq) {
		this.asyncReq = asyncReq;
	}
	/**
	 * 共享文件, 实际就是文件下载
	 * @param inputStream 	文件的流
	 * @param filename 		设置文件名 和 后缀名(也就是文件的类型)
	 */
	public void shareFile(FileInputStream inputStream, String filename, String suffix) {
		asyncReq.sendFile(inputStream, filename + suffix);
	}
	/**
	 * 共享文件, 实际就是文件下载
	 * @param inputStream 	文件的流
	 * @param filename 		设置文件名  注意这里的文件名需要后缀名
	 */
	public void shareFile(FileInputStream inputStream, String filename) {
		asyncReq.sendFile(inputStream, filename);
	}
	/**
	 * 共享文件, 提供给别人下载
	 * @param file 想要共享的文件
	 */
	public void shareFile(File file) {
		try {
			asyncReq.sendFile(new FileInputStream(file), file.getName());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}
