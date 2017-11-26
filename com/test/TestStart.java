package com.test;


import java.io.IOException;

import com.nymph.annotation.GET;
import com.nymph.annotation.HTTP;
import com.nymph.annotation.Injection;
import com.nymph.annotation.JSON;
import com.nymph.annotation.POST;
import com.nymph.annotation.Serialize;
import com.nymph.annotation.UrlHolder;
import com.nymph.start.MainStarter;
import com.nymph.transfer.Multipart;
import com.nymph.transfer.Multipart.FileInf;
import com.nymph.transfer.Transfer;

@HTTP("/demo")
@Starter
public class TestStart {

	private @Injection Man man;

	@GET("/yes/@test/@demo")
	public String test(@UrlHolder("test") String field, @UrlHolder String demo) {
		return "/index";
	}

	@POST("/no")
	@JSON
	public Man test2() {
		return man;
	}
	
	@GET("/test")
	public String test3(String url, Transfer transfer) {
		transfer.ofRequest("man", man);
		return "/index";
	}
	
	@GET("/class")
	@Serialize
	public Man test4() throws IOException {
		return new Man("刘德华");
	}
	
	@GET("/upload")
	public void test5(Multipart multipart) throws IOException {
		// file表示页面input标签的name
		FileInf fileInf = multipart.getFileInf("file");
		fileInf.writeTo("c:/data/demo.jpg");
	}

	public static void main(String[] args) throws Exception {
		MainStarter.start(TestStart.class);
	}
}
