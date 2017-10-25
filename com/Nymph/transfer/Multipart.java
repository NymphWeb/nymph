package com.nymph.transfer;

import com.nymph.utils.BasicUtil;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 文件上传的相关操作
 * @author NYMPH
 * @date 2017年10月7日下午8:27:58
 */
public final class Multipart {
	
	private static final Log LOG = LogFactory.getLog(Multipart.class);
	
	private final Map<String, FileInf> fileInfo = new HashMap<>();
	
	public void addFiles(FileItem fileItem) {
		try {
			fileInfo.put(fileItem.getFieldName(), 
					new FileInf(fileItem.getName(), fileItem.getInputStream()));
		} catch (Exception e) {
			LOG.error(null, e);
		}
	}
	
	/**
	 * 通过页面input标签的name属性获取FileInf
	 * @param fieldName
	 * @return
	 */
	public FileInf getFileInf(String fieldName) {
		return fileInfo.get(fieldName);
	}
	/**
	 * 页面的所有的input标签的type=file 的 name属性值
	 * @return
	 */
	public List<String> fields() {
		return fileInfo.keySet().stream().collect(Collectors.toList());
	}
	
	/**
	 * 获取所有表示文件的FileInf
	 * @return
	 */
	public List<FileInf> fileInfs() {
		return fileInfo.values().stream().collect(Collectors.toList());
	}
	/**
	 * 根据input的name属性获取到对应的文件流
	 * @param field
	 * @return
	 */
	public FileInputStream getInputStream(String field) {
		return (FileInputStream)fileInfo.get(field).getStream();
	}
	
	public static class FileInf {
		final String fileName;
		
		final InputStream input;
		
		public FileInf(String fileName, InputStream input) {
			this.fileName = fileName;
			this.input = input;
		}
		
		public FileInputStream getStream() {
			return (FileInputStream)input;
		}
		
		public File writeTo(String location) {
			FileChannel channel = null;
			FileOutputStream out = null;
			try {
				channel = getStream().getChannel();
				out = new FileOutputStream(location);
				channel.transferTo(0, channel.size(), Channels.newChannel(out));
				return new File(location);
			} catch (IOException e) {
				LOG.error(null, e);
				return null;
			} finally {
				BasicUtil.closed(channel, out);
			}
		}
		
		/**
		 * 根据name属性获取 上传的文件的真实名称
		 * @return
		 */
		public String getFileName() {
			return fileName;
		}
		
		/**
		 * 根据name属性获取 上传的文件的后缀名
		 * @return
		 */
		public String getFileSuffix() {
			if (fileName.indexOf(".") < 0) {
				return "";
			}
			return fileName.substring(0, fileName.lastIndexOf("."));
		}
	}

}
